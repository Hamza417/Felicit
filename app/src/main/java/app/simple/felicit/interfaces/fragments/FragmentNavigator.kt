package app.simple.felicit.interfaces.fragments

import app.simple.felicit.medialoader.mediamodels.AudioContent

interface FragmentNavigator {
    fun navigateTo (fragmentIndexValue: Int, audioContent: ArrayList<AudioContent>)
}