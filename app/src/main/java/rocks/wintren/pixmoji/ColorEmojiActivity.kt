package rocks.wintren.pixmoji

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
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

        viewModel.start()
        binding.emoji = "\uD83E\uDDC4"
    }
}

@BindingAdapter("addTextViews")
fun bindingAddTextViews(layout: LinearLayout, textList: List<String>?) {
    textList?.forEach { emoji ->
        val textView = TextView(layout.context).apply {
            text = emoji
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }
        layout.addView(textView)
    }
}