package com.rishinali.rawchat.model

data class SignedResult(
    val status: Status,
    val message: String?
) {
    companion object {

        fun signingIn(): Result {
            return Result(
                Status.SIGNING_IN,
                null
            )
        }

        fun signedIn(): Result {
            return Result(
                Status.SIGNED_IN,
                null
            )
        }

        fun signedOut(): Result {
            return Result(
                Status.SIGNED_OUT,
                null
            )
        }

        fun signingError(message: String?): Result {
            return Result(
                Status.SIGNING_ERROR,
                message
            )
        }
    }
}
