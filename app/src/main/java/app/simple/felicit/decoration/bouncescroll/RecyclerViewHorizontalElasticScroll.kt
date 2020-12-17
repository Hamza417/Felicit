package app.simple.felicit.decoration.bouncescroll

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.decoration.customholders.HorizontalListViewHolder
import app.simple.felicit.decoration.customholders.VerticalListViewHolder

object RecyclerViewHorizontalElasticScroll {
    inline fun <reified T : HorizontalListViewHolder> RecyclerView.setupHorizontalEdgeEffectFactory() {
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
                         * We simply update the view properties without animation.
                         */
                        val sign = if (direction == DIRECTION_LEFT) -1 else 1
                        val rotationDelta = sign * deltaDistance * ScrollConstants.overscrollRotationMagnitude
                        val translationYDelta = sign * recyclerView.height * deltaDistance * ScrollConstants.overscrollTranslationMagnitude

                        recyclerView.forEachVisibleHolder { holder: HorizontalListViewHolder ->
                            if (holder is T) {
                                holder.rotation.cancel()
                                holder.translationX.cancel()
                                holder.itemView.rotation += rotationDelta
                                holder.itemView.translationX += translationYDelta
                            }
                        }
                    }

                    override fun onRelease() {
                        super.onRelease()
                        /*
                         * The finger is lifted. This is when we should start the animations to bring
                         * the view property values back to their resting states.
                         */
                        recyclerView.forEachVisibleHolder { holder: HorizontalListViewHolder ->
                            if (holder is T) {
                                holder.rotation.start()
                                holder.translationX.start()
                            }
                        }
                    }

                    override fun onAbsorb(velocity: Int) {
                        super.onAbsorb(velocity)
                        val sign = if (direction == DIRECTION_LEFT) -1 else 1
                        // The list has reached the edge on fling.
                        val translationVelocity = sign * velocity * ScrollConstants.flingTranslationMagnitude
                        recyclerView.forEachVisibleHolder { holder: HorizontalListViewHolder ->
                            if (holder is T) {
                                holder.translationX
                                    .setStartVelocity(translationVelocity)
                                    .start()
                            }
                        }
                    }
                }
            }
        }
    }

    inline fun <reified T : HorizontalListViewHolder> RecyclerView.forEachVisibleHolder(action: (T) -> Unit) {
        for (i in 0 until childCount) {
            action(getChildViewHolder(getChildAt(i)) as T)
        }
    }
}