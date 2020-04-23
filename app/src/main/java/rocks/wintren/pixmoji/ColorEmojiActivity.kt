package rocks.wintren.pixmoji

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

        testColorMap(binding)

    }

    private fun testColorMap(binding: ActivityColorEmojiBinding) {
        val fillerMap = ColorEmojiMap()
        emojis.forEach { (color, emoji) -> fillerMap.addEmoji(color, emoji) }
        fillerMap.finalise()

        val closestEmoji = fillerMap.getClosest(Color.BLUE)

        binding.emoji = closestEmoji.random()

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