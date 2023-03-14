package com.skyd.raca.ui.screen.settings.searchconfig

import com.skyd.raca.base.IUiIntent
import com.skyd.raca.model.bean.SearchDomainBean

sealed class SearchConfigIntent : IUiIntent {
    object GetSearchDomain : SearchConfigIntent()
    data class SetSearchDomain(val searchDomainBean: SearchDomainBean) : SearchConfigIntent()
}