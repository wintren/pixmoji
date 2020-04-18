package rocks.wintren.pixmoji

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main2.*
import rocks.wintren.pixmoji.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun measureEmojis() {
        val factory = EmojiBitmapFactory(EmojiBitmapFactory.EmojiScale.Large)

        bitmapEmojis.children.toList().forEach {
            if (it !is ImageView) return@forEach
            val bitmap = factory.createEmoji("↙")
            it.setImageBitmap(bitmap)
        }

        bitmapEmojis.onLayout {
            bitmapEmojis.children.toList().forEach { view ->
                if (view !is ImageView) return@forEach
                d("view: ${view.width}x${view.height}")
                view.drawable.let {
                    d("drawable: ${it.intrinsicWidth}x${it.intrinsicHeight}")
                }

            }
        }
    }

}
