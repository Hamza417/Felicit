package app.simple.felicit.decoration.customholders

import android.view.View
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

open class VerticalListHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var currentVelocity = 0f

    /**
     * A [SpringAnimation] for this RecyclerView item. This animation rotates the view with a bouncy
     * spring configuration, resulting in the oscillation effect.
     *
     * The animation is started in [RecyclerView.addOnScrollListener].
     */
    val rotation: SpringAnimation = SpringAnimation(itemView, SpringAnimation.ROTATION)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(VerticalListViewHolder.bouncyValue)
                .setStiffness(VerticalListViewHolder.stiffnessValue)
        )
        .addUpdateListener { _, _, velocity ->
            currentVelocity = velocity
        }

    /**
     * A [SpringAnimation] for this RecyclerView item. This animation is used to bring the item back
     * after the over-scroll effect.
     */
    val translationY: SpringAnimation = SpringAnimation(itemView, SpringAnimation.TRANSLATION_Y)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(VerticalListViewHolder.bouncyValue)
                .setStiffness(VerticalListViewHolder.stiffnessValue)
        )

    val scaleY: SpringAnimation = SpringAnimation(itemView, SpringAnimation.SCALE_Y)
        .setSpring(
            SpringForce()
                .setFinalPosition(1f)
                .setDampingRatio(VerticalListViewHolder.bouncyValue)
                .setStiffness(VerticalListViewHolder.stiffnessValue)
        )

    val scaleX: SpringAnimation = SpringAnimation(itemView, SpringAnimation.SCALE_X)
        .setSpring(
            SpringForce()
                .setFinalPosition(1f)
                .setDampingRatio(VerticalListViewHolder.bouncyValue)
                .setStiffness(VerticalListViewHolder.stiffnessValue)
        )
}