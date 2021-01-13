package app.simple.felicit.exceptions

class NotABackgroundThreadException : Exception(reason) {
    companion object {
        private const val reason = "This function performs blocking calls and requires to be run in a background thread"
    }
}