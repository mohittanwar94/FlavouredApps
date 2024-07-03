package com.qureka.skool.ipchecker.model

data class ConsentRequest(
    var ga_id: String? = null,
    var app_version: String? = null,
    var status: String? = null,
    var country_code:String? = null,
    var app_id: String? = null,
)
