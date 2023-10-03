package com.example.firebasechat.utils

import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ForwardToSettingsCallback
import com.permissionx.guolindev.callback.RequestCallback
import com.permissionx.guolindev.request.ForwardScope

object CommonPermissionUtils {
    fun requestCommonPermission(
        activity: FragmentActivity?,
        permissionList: List<String?>?,
        allPermissionApprovedListener: Runnable?,
        anyPermissionDenyListener: Runnable?
    ) {
        PermissionX.init(activity!!)
            .permissions(permissionList as List<String>)
            .onForwardToSettings(ForwardToSettingsCallback { scope: ForwardScope, deniedList: List<String?>? ->
                scope.showForwardToSettingsDialog(
                    deniedList as List<String>,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            })
            .request(RequestCallback { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    allPermissionApprovedListener?.run()
                } else {
                    anyPermissionDenyListener?.run()
                }
            })
    }
}