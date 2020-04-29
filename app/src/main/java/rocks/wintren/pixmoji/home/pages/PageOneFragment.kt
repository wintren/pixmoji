package rocks.wintren.pixmoji.home.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import rocks.wintren.pixmoji.R
import rocks.wintren.pixmoji.databinding.FragmentPageOneBinding
import rocks.wintren.pixmoji.home.HomeActivityViewModel

class PageOneFragment() : Fragment() {

    lateinit var parentViewModel: HomeActivityViewModel
    lateinit var pageViewModel: PageOneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        pageViewModel = ViewModelProvider(this).get(PageOneViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentPageOneBinding>(
            inflater,
            R.layout.fragment_page_one,
            container,
            false
        ).run {
            viewModel = pageViewModel
            lifecycleOwner = this@PageOneFragment
            root
        }
    }


}