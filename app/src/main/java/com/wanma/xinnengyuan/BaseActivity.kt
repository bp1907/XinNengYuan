package com.wanma.xinnengyuan

import android.app.ProgressDialog
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.wanma.xinnengyuan.utils.PhotoViewUtil
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {

    lateinit var disposable: Disposable
    lateinit var mdialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if(!disposable.isDisposed) {
            disposable.dispose()
        }
        PhotoViewUtil.clearPicCache()
    }
}