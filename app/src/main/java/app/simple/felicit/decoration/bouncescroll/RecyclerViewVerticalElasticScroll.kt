package app.simple.felicit.decoration.bouncescroll

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.decoration.customholders.VerticalListViewHolder
import app.simple.felicit.decoration.bouncescroll.ScrollConstants.flingTranslationMagnitude
import app.simple.felicit.decoration.bouncescroll.ScrollConstants.overscrollRotationMagnitude
import app.simple.felicit.decoration.bouncescroll.ScrollConstants.overscrollTranslationMagnitude

object RecyclerViewVerticalElasticScroll {

    inline fun <reified T : VerticalListViewHolder, reified K : VerticalListViewHolder> RecyclerView.setupEdgeEffectFactory() {
        this.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {

                return object : EdgeEffect(recyclerView.context) {
                    override fun onPull(deltaDistance: Float) {
                        super.onPull(deltaDistance)
                        handlePull(deltaDistance)
                    }

                    override fun onPull(deltaDistance: Float, displacement: Float) {
                        super.onPull(deltaDistance, displacement)
                        handlePull(deltaDistance)
                    }

                    private fun handlePull(deltaDistance: Float) {
                        /*
                         * This is called on every touch event while the list is scrolled with a finger.
                         * simply update the view properties without animation.
                         */
                        val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                        val rotationDelta = sign * deltaDistance * overscrollRotationMagnitude
                        val translationYDelta = sign * recyclerView.width * deltaDistance * overscrollTranslationMagnitude

                        recyclerView.forEachVisibleHolder { holder: VerticalListViewHolder ->
                            if (holder is T) {
                                holder.rotation.cancel()
                                holder.translationY.cancel()
                                holder.itemView.rotation += rotationDelta
                                holder.itemView.translationY += translationYDelta
                            } else if (holder is K) {
                                holder.rotation.cancel()
                                holder.translationY.cancel()
                                holder.itemView.rotation += rotationDelta
                                holder.itemView.translationY += translationYDelta
                            }
                        }
                    }

                    override fun onRelease() {
                        super.onRelease()
                        /*
                         * The finger is lifted. This is when we should start the animations to bring
                         * the view property values back to their resting states.
                         */
                        recyclerView.forEachVisibleHolder { holder: VerticalListViewHolder ->
                            if (holder is T) {
                                holder.rotation.start()
                                holder.translationY.start()
                            } else if (holder is K) {
                                holder.rotation.start()
                                holder.translationY.start()
                            }
                        }
                    }

                    override fun onAbsorb(velocity: Int) {
                        super.onAbsorb(velocity)
                        val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                        // The list has reached the edge on fling.
                        val translationVelocity = sign * velocity * flingTranslationMagnitude
                        recyclerView.forEachVisibleHolder { holder: VerticalListViewHolder ->
                            if (holder is T) {
                                holder.translationY
                                    .setStartVelocity(translationVelocity)
                                    .start()
                            } else if (holder is K) {
                                holder.translationY
                                    .setStartVelocity(translationVelocity)
                                    .start()
                            }
                        }
                    }
                }
            }
        }
    }

    inline fun <reified T : VerticalListViewHolder> RecyclerView.forEachVisibleHolder(action: (T) -> Unit) {
        for (i in 0 until childCount) {
            action(getChildViewHolder(getChildAt(i)) as T)
        }
    }
}