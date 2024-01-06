package de.makuhn.semesterticket.ui.fullsizeticket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import de.makuhn.semesterticket.R
import java.io.File

class FullSizeImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullsize_image)
        setWindowBrightnessToHigh()

        val photoView: PhotoView = findViewById(R.id.photoView)
        photoView.maximumScale = 10f

        // Get the image URL or resource from the intent
        // For example, assuming you passed the URL as an extra with key "imageUrl"
        val imageUrl = intent.getStringExtra("imageUrl")
        val image = File(filesDir, imageUrl!!)


        photoView.setImageURI(image.toUri())

        // glide makes image blurry when zoomed in
//        Glide.with(this)
//            .load(image)
//            .skipMemoryCache(true)
//            .into(photoView)

    }

    override fun onPause() {
        super.onPause()
        resetWindowBrightness()
    }


    private fun setWindowBrightnessToHigh() {
        val lp = this.window.attributes
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        this.window.attributes = lp
    }
    private fun resetWindowBrightness() {
        val lp = this.window.attributes
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        this.window.attributes = lp
    }
}