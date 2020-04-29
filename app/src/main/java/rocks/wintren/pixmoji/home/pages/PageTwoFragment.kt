package rocks.wintren.pixmoji.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import rocks.wintren.pixmoji.R
import rocks.wintren.pixmoji.databinding.FragmentPageTwoBinding
import rocks.wintren.pixmoji.home.HomeActivityViewModel

class PageTwoFragment : Fragment() {

    lateinit var parentViewModel: HomeActivityViewModel
    lateinit var pageViewModel: PageTwoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        pageViewModel = ViewModelProvider(this).get(PageTwoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentPageTwoBinding>(inflater, R.layout.fragment_page_two, container, false).run {
            viewModel = pageViewModel
            lifecycleOwner = this@PageTwoFragment
            root
        }
    }

}