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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_color_emoji.*
import rocks.wintren.pixmoji.adapter.BindingAdapterItem
import rocks.wintren.pixmoji.adapter.SimpleBindingAdapter
import rocks.wintren.pixmoji.databinding.ActivityColorEmojiBinding
import java.util.*

class ColorEmojiActivity : AppCompatActivity() {

    private lateinit var viewModel: ColorEmojiViewModel

    data class HexStringColor(val hexString: String, val color: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityColorEmojiBinding>(this, R.layout.activity_color_emoji)
        viewModel = ViewModelProvider(this).get(ColorEmojiViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.start()

//        testColorMap(binding)

        val adapter = SimpleBindingAdapter()
        histogram.adapter = adapter
        histogram.layoutManager = LinearLayoutManager(this)

        EmojiRepository.ready
            .filter { it }
            .observeOn(computationThread)
            .map {
                val colorComparator = ColorComparator()
                EmojiRepository.colorEmojiMap.colorEmojiMap
                    .flatMap { entry ->
                        entry.value.map { it to entry.key }
                    }
                    .map { HexStringColor(it.first, it.second) }
            }

            .observeOn(mainThread)
            .subscribe { emojis ->
                    emojis.map { BindingAdapterItem(R.layout.item_color_debug, BR.item, it) }
                    .let { adapter.submitList(it) }

            }


    }

    val randomColor: Int get() = (Color.BLACK..Color.WHITE).random()

    private fun testColorMap(binding: ActivityColorEmojiBinding) {
        val fillerMap = ColorEmojiMap()
        emojis.forEach { (color, emoji) -> fillerMap.addEmoji(color, emoji) }
//        fillerMap.finalise()

        val closestEmoji = fillerMap.getEmojisForColor(Color.BLUE)

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