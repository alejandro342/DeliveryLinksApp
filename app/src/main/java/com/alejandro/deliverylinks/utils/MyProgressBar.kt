package com.alejandro.deliverylinks.utils

interface MyProgressBar {

    interface progress{
        fun showProgressBar()
        fun hideProgressBar()
    }
    fun showBottom()
    fun hideBottom()
}