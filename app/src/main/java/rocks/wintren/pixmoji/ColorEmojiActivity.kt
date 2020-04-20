package rocks.wintren.pixmoji

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import rocks.wintren.pixmoji.databinding.ActivityColorEmojiBinding

class ColorEmojiActivity : AppCompatActivity() {

    private lateinit var viewModel: ColorEmojiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityColorEmojiBinding>(this, R.layout.activity_color_emoji)
        viewModel = ViewModelProvider(this).get(ColorEmojiViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

    }
}
