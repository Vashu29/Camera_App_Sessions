package com.example.camera_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import java.io.Reader

class MainActivity : AppCompatActivity() {

    lateinit var dbHelper: SessionDatabaseHelper
    private var currentSessionId: String? = null
    //private var currentSessionId: String? = null
    lateinit var capReq: CaptureRequest.Builder
    lateinit var handler: Handler
    lateinit var handlerThread: HandlerThread
    lateinit var cameraManager: CameraManager
    lateinit var textureView: TextureView
    lateinit var cameraCaptureSession: CameraCaptureSession
    lateinit var cameraDevice: CameraDevice
    lateinit var captureRequest: CaptureRequest
    lateinit var imageReader: ImageReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        get_permissions()

        dbHelper = SessionDatabaseHelper(this)

        val sessionBtn = findViewById<Button>(R.id.sessionID)
        sessionBtn.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_session, null)
            val etSessionId = dialogView.findViewById<EditText>(R.id.etSessionId)
            val etName = dialogView.findViewById<EditText>(R.id.etName)
            val etAge = dialogView.findViewById<EditText>(R.id.etAge)

            AlertDialog.Builder(this)
                .setTitle("New Session")
                .setView(dialogView)
                .setPositiveButton("OK") { dialog, _ ->
                    val enteredId = etSessionId.text.toString().trim()
                    val enteredName = etName.text.toString().trim()
                    val enteredAge = etAge.text.toString().trim()

                    if (enteredId.isNotEmpty() && enteredName.isNotEmpty() && enteredAge.isNotEmpty()) {
                        currentSessionId = enteredId+", "+enteredName+", "+enteredAge

                        val success = dbHelper.insertSession(
                            enteredId,
                            enteredName,
                            enteredAge.toInt()
                        )

                        if (success) {
                            Toast.makeText(
                                this,
                                "Session saved → ID: $enteredId, Name: $enteredName, Age: $enteredAge",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(this, "Failed to save session", Toast.LENGTH_SHORT).show()
                        }

                        val baseDir = File(getExternalMediaDirs()[0], "Sessions/$currentSessionId")
                        if (!baseDir.exists()) baseDir.mkdirs()

                    } else {
                        Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        val viewBtn = findViewById<Button>(R.id.view)
        viewBtn.setOnClickListener {
            val intent = Intent(this, ViewSessionsActivity::class.java)
            startActivity(intent)
        }

        textureView = findViewById(R.id.textureView)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler((handlerThread).looper)
        textureView.surfaceTextureListener = object: TextureView.SurfaceTextureListener{

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                open_Camera()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) { TODO("Not yet implemented") }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean { return false }
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) { }
        }

        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1)

        imageReader.setOnImageAvailableListener(object: ImageReader.OnImageAvailableListener{
            override fun onImageAvailable(p0: ImageReader?) {

                var image = p0?.acquireLatestImage()
                var buffer = image!!.planes[0].buffer
                var bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)

                val sessionId = currentSessionId ?: run {
                    Toast.makeText(this@MainActivity, "Please set Session ID first", Toast.LENGTH_SHORT).show()
                    image.close()
                    return
                }
                // 🔹 Path: Android/media/<AppName>/Sessions/<SessionID>/
                val baseDir = File(getExternalMediaDirs()[0], "Sessions/$sessionId")
                if (!baseDir.exists()) {
                    baseDir.mkdirs()
                    Log.d("SAVE_IMAGE", " Created folder: ${baseDir.absolutePath}")
                } else {
                    Log.d("SAVE_IMAGE", " Folder already exists: ${baseDir.absolutePath}")
                }
                // 🔹 Unique filename
                val filename = "IMG_${System.currentTimeMillis()}.jpeg"
                val file = File(baseDir, filename)


                var opStream = FileOutputStream(file)
                opStream.write(bytes)
                opStream.close()
                image.close()

                Toast.makeText(this@MainActivity, "image captured", Toast.LENGTH_SHORT).show()

            }
        }, handler)

        findViewById<Button>(R.id.capture).apply{
            setOnClickListener{
                capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                capReq.addTarget(imageReader.surface)
                cameraCaptureSession.capture(capReq.build(), null, null)
            }
        }
    }

    fun open_Camera(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        cameraManager.openCamera(cameraManager.cameraIdList[0], object: CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                var surface = Surface(textureView.surfaceTexture)
                capReq.addTarget(surface)
                
                cameraDevice.createCaptureSession(listOf(surface,imageReader.surface), object :
                    CameraCaptureSession.StateCallback() {
                    override fun onConfigured(p0: CameraCaptureSession) {
                        cameraCaptureSession = p0
                        cameraCaptureSession.setRepeatingRequest(capReq.build(), null, null)
                    }
                    override fun onConfigureFailed(session: CameraCaptureSession) { }
                }, handler)
            }
            override fun onDisconnected(camera: CameraDevice) { }
            override fun onError(camera: CameraDevice, error: Int) { }
        }, handler)
    }


    fun get_permissions(){
        var permissionList = mutableListOf<String>()
        if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(android.Manifest.permission.CAMERA)
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if(permissionList.size > 0){
            requestPermissions(permissionList.toTypedArray(), 101)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                get_permissions()
            }
        }
    }
}
