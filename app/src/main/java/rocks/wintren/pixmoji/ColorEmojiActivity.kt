package rocks.wintren.pixmoji

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.emoji.widget.EmojiTextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_color_emoji.*
import rocks.wintren.pixmoji.adapter.BindingAdapterItem
import rocks.wintren.pixmoji.adapter.SimpleBindingAdapter
import rocks.wintren.pixmoji.databinding.ActivityColorEmojiBinding

class ColorEmojiActivity : AppCompatActivity() {

    private lateinit var viewModel: ColorEmojiViewModel

    data class HexStringColor(val color: Int, val emojis: List<String>)

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
                EmojiRepository.colorEmojiMap.colorEmojiMap
                    .map { entry ->
                    HexStringColor(
                        entry.key,
                        entry.value.map { " (${it.category}:${it.name}) ${it.emoticon}" }
                    )
                }.sortedBy { it.emojis.first() }
            }
            .observeOn(mainThread)
            .subscribe { emojis ->
                emojis.map {
                    BindingAdapterItem(R.layout.item_color_debug, BR.item, it)
                }
                    .let { adapter.submitList(it) }
            }


    }

}


@BindingAdapter("addTextViews")
fun bindingAddTextViews(layout: LinearLayout, textList: List<String>?) {
    layout.removeAllViews()
    textList?.forEach { emoji ->
        val textView = EmojiTextView(layout.context).apply {
            text = emoji
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }
        layout.addView(textView)
    }
}
