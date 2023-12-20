package com.snuzj.myapplication

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var myWebView: WebView
    private val TAG = "TEST"
    private var mPermissionRequest: PermissionRequest? = null

    private val REQUEST_CAMERA_PERMISSION = 1
    private val PERM_CAMERA = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myWebView = WebView(this)

        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.allowFileAccessFromFileURLs = true
        myWebView.settings.allowUniversalAccessFromFileURLs = true

        myWebView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                Log.i(TAG, "onPermissionRequest")
                mPermissionRequest = request
                val requestedResources = request.resources
                for (r in requestedResources) {
                    if (r == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
                            .setTitle("Allow Permission to camera")
                            .setPositiveButton("Allow") { dialog, which ->
                                dialog.dismiss()
                                mPermissionRequest?.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                                Log.d(TAG, "Granted")
                            }
                            .setNegativeButton("Deny") { dialog, which ->
                                dialog.dismiss()
                                mPermissionRequest?.deny()
                                Log.d(TAG, "Denied")
                            }
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                        break
                    }
                }
            }

            override fun onPermissionRequestCanceled(request: PermissionRequest) {
                super.onPermissionRequestCanceled(request)
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        if (hasCameraPermission()) {
            myWebView.loadUrl("file:///android_asset/index.html")
            setContentView(myWebView)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera so you can take pictures.",
                REQUEST_CAMERA_PERMISSION,
                *PERM_CAMERA
            )
        }
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, *PERM_CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // Handle permissions granted
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // Handle permissions denied
    }
}
