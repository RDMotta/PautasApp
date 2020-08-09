package com.rdm.uds.pautas.app

import android.content.pm.PackageManager
import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.checkSelfPermission


class PermissionUtils {
    companion object {
        fun permissionGranted(
                requestCode: Int, permissionCode: Int, grantResults: IntArray): Boolean {
            return if (requestCode == permissionCode) {
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            } else false
        }

        fun requestPermission(
                activity: Activity, requestCode: Int, vararg permissions: String): Boolean {
            var granted = true
            val permissionsNeeded = ArrayList<String>()

            for (s in permissions) {
                val permissionCheck = ActivityCompat.checkSelfPermission(activity, s)
                val hasPermission = permissionCheck == PackageManager.PERMISSION_GRANTED
                granted = granted and hasPermission
                if (!hasPermission) {
                    permissionsNeeded.add(s)
                }
            }

            return if (granted) {
                true
            } else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                                permissionsNeeded[0])) {

                    ActivityCompat.requestPermissions(activity,
                            permissionsNeeded.toArray(arrayOfNulls<String>(permissionsNeeded.size)),
                            requestCode)
                    false
                } else true
            }
        }
    }

}