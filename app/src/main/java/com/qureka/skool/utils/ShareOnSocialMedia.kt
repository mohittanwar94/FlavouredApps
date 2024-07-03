package com.qureka.skool.utils

import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.qureka.skool.ServerConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ShareOnSocialMedia(var context: Context) {


    var WhatsUpPackage = "com.whatsapp"
    var Facebookackage = "com.facebook.katana"
    var SnapChat = "com.snapchat.android"
    var Twitter = "com.twitter.android"
    var Instagram = "com.instagram.android"

    companion object {
        private val TAG = ShareOnSocialMedia::class.java.simpleName

        @Volatile
        private var mInstance: ShareOnSocialMedia? = null

        public fun get(context: Context): ShareOnSocialMedia = mInstance ?: synchronized(this) {
            val newInstance = mInstance ?: ShareOnSocialMedia(context).also { mInstance = it }
            newInstance
        }
    }


    fun sendMessageonWhatsApp(message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, message)
//        if (intent.resolveActivity(context.packageManager) == null) {
//            Log.d(TAG,"whatsapp is not installed")
//            return
//        }

        if (isAppInstalled("com.whatsapp")) {
            context.startActivity(intent)
        } else {
            Log.d(TAG, "whatsapp is not installed")
            return
        }


    }


    fun shareText(message: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "QurekaSkool App Subject")
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }


    fun shareTextUrl(packagename: String, shareMessage: String) {

        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        share.putExtra(Intent.EXTRA_SUBJECT, "Qureka App Subject")
        share.putExtra(Intent.EXTRA_TEXT, shareMessage)
        if (packagename == WhatsUpPackage) {
            if (isAppInstalled(WhatsUpPackage)) {
                share.setPackage(WhatsUpPackage)
            } else {
                Toast.makeText(context, "whatsapp is not installed", Toast.LENGTH_LONG).show()
                return
            }
        }
        if (packagename == Facebookackage) {
            if (isAppInstalled(Facebookackage)) {
                share.setPackage(Facebookackage)
            } else {
                // show Toast
                Toast.makeText(context, "facebook is not installed", Toast.LENGTH_LONG).show()
                return
            }
        }


        if (packagename == Twitter) {
            if (isAppInstalled(Twitter)) {
                share.setPackage(Twitter)
            } else {
                // show Toast
                Toast.makeText(context, "twiter is not installed", Toast.LENGTH_LONG).show()
                return
            }
        }

        if (packagename == Instagram) {
            if (isAppInstalled(Instagram)) {
                share.setPackage(Instagram)
            } else {
                // show Toast
                Toast.makeText(context, "instagram is not installed", Toast.LENGTH_LONG).show()
                return
            }
        }

        if (packagename == SnapChat) {
            if (isAppInstalled(SnapChat)) {
                val intentComponent = ComponentName(
                    "com.snapchat.android", "com.snapchat.android.LandingPageActivity"
                )
                share.setComponent(intentComponent)
                share.setPackage(SnapChat)
                //    context.grantUriPermission(InstagramPackage, backgroundAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                Toast.makeText(context, "snapchat is not installed", Toast.LENGTH_LONG).show()
                return
            }
        }

        // share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (share.resolveActivity(context.packageManager) != null) {
            context.startActivity(share)
        } else {
            if (packagename == WhatsUpPackage) {
                Toast.makeText(
                    context, "Please Enable Whatsapp", Toast.LENGTH_LONG
                ).show()
            } else if (packagename == Facebookackage) {
                Toast.makeText(
                    context, "Please Enable FaceBook", Toast.LENGTH_LONG
                ).show()
            } else if (packagename == SnapChat) {
                Toast.makeText(
                    context, "Please Enable SnapChat", Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    fun shareTextViaWhatsApp(activity: Context, text: String) {
        try {
            val uriUrl = Uri.parse("whatsapp://send?text=$text")
            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
            activity.startActivity(launchBrowser)
        } catch (e: Exception) {
            Toast.makeText(context, "whatsapp is not installed", Toast.LENGTH_LONG).show()
        }

    }


    fun shareChooseMultipleOption(message: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }


    fun shareTextViaInstagram(activity: Context, message: String) {
        val pm = activity.packageManager
        try {
            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "image/*"
            val info = pm.getPackageInfo("com.instagram.android", PackageManager.GET_META_DATA)
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.instagram.android")
            waIntent.putExtra(Intent.EXTRA_TEXT, message)
            //waIntent.putExtra(Intent.EXTRA_HTML_TEXT, ShareUtils.appendPlayStoreUrlToMessage(text))
            activity.startActivity(
                Intent.createChooser(waIntent, "Share via")
            )
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(
                activity, "instagram is not installed", Toast.LENGTH_SHORT
            ).show()
        }

    }


    fun shareTwitter(message: String) {
        val tweetIntent = Intent(Intent.ACTION_SEND)
        tweetIntent.putExtra(Intent.EXTRA_TEXT, message)
        tweetIntent.type = "text/plain"
        val packManager = context.packageManager
        val resolvedInfoList =
            packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY)
        var resolved = false
        for (resolveInfo in resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                    resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name
                )
                resolved = true
                break
            }
        }
        if (resolved) {
            context.startActivity(tweetIntent)
        } else {
            val i = Intent()
            i.putExtra(Intent.EXTRA_TEXT, message)
            i.action = Intent.ACTION_VIEW
            i.data = Uri.parse("https://twitter.com/intent/tweet?text=" + message)
            context.startActivity(i)
            //Toast.makeText(this, "Twitter app isn't found", Toast.LENGTH_LONG).show()
        }
    }


    private fun isAppInstalled(packageName: String): Boolean {
        val pm = context.getPackageManager()
        var app_installed: Boolean
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        }
        return app_installed
    }


    fun shareTextViaWhatsApp(activity: Activity, text: String) {
        try {
            val uriUrl = Uri.parse("whatsapp://send?text=$text")
            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl);
            activity.startActivity(launchBrowser)
        } catch (e: Exception) {
            Toast.makeText(activity, "whatsapp not installed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveImageInLegacy(bitmap: Bitmap): Uri {
        val filename = "IMG_Share.jpg"
        val directory = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
        val file = File(directory, filename)
        if (file.exists()) file.delete()
        file.createNewFile()
        val outStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        outStream.flush()
        outStream.close()
        MediaScannerConnection.scanFile(
            context, arrayOf(file.absolutePath), null, null
        )
        return FileProvider.getUriForFile(
            context, ServerConfig.provider, file
        )
    }


    fun textWithImage(context: Context, message: String, splashLogo: Int) {
        val baos = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeResource(context.resources, splashLogo)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        val decodedString: ByteArray = Base64.decode(
            imageString, Base64.DEFAULT
        )

        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        val imageToShare = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageInQ(decodedByte)
        } else saveImageInLegacy(decodedByte)


        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_STREAM, imageToShare)
            type = "text/plain"
            type = "image/*"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    private fun saveImageInQ(bitmap: Bitmap): Uri {
        var imageUri: Uri? = null
        try {
            val filename = "IMG_Share.jpg"
            val photoLcl =
                File("${Environment.getExternalStorageDirectory()}/$DIRECTORY_PICTURES/$filename")
            val imageUriLcl = FileProvider.getUriForFile(
                context, ServerConfig.provider, photoLcl
            )
            val contentResolver = context.contentResolver
            contentResolver.delete(imageUriLcl, null, null)
            var fos: OutputStream? = null
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, DIRECTORY_PICTURES)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
            contentResolver.also { resolver ->
                imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
            fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }
            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
            imageUri?.let { contentResolver.update(it, contentValues, null, null) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageUri!!
    }
}