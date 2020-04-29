package rocks.wintren.pixmoji.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import rocks.wintren.pixmoji.R
import rocks.wintren.pixmoji.databinding.FragmentPageFourBinding
import rocks.wintren.pixmoji.home.HomeActivityViewModel

class PageFourFragment : Fragment() {

    lateinit var parentViewModel: HomeActivityViewModel
    lateinit var pageViewModel: PageFourViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        pageViewModel = ViewModelProvider(this).get(PageFourViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentPageFourBinding>(
            inflater,
            R.layout.fragment_page_four,
            container,
            false
        ).run {
            viewModel = pageViewModel
            lifecycleOwner = this@PageFourFragment
            root
        }
    }

}