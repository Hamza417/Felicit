package app.simple.felicit.interfaces.fragments

import app.simple.felicit.models.AudioContent

interface FragmentNavigator {
    fun navigateTo (fragmentIndexValue: Int, audioContent: ArrayList<AudioContent>)
}