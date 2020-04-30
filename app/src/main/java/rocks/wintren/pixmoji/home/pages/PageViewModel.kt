package rocks.wintren.pixmoji.home.pages

import rocks.wintren.pixmoji.BaseViewModel
import rocks.wintren.pixmoji.home.HomeActivityViewModel

abstract class PageViewModel : BaseViewModel() {

    lateinit var parent: HomeActivityViewModel

}