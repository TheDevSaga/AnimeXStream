package net.xblacky.animexstream.utils.epoxy

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import net.xblacky.animexstream.R
import net.xblacky.animexstream.utils.model.AnimeMetaModel

@EpoxyModelClass(layout = R.layout.recycler_anime_common)
abstract class AnimeCommonModel : EpoxyModelWithHolder<AnimeCommonModel.MovieHolder>() {

    @EpoxyAttribute
    lateinit var animeMetaModel: AnimeMetaModel

    @EpoxyAttribute
    var clickListener: View.OnClickListener? = null

    override fun bind(holder: MovieHolder) {
        Glide.with(holder.animeImageView.context).load(animeMetaModel.imageUrl).transition(
            DrawableTransitionOptions.withCrossFade()
        ).into(holder.animeImageView)
        holder.animeTitle.text = animeMetaModel.title
        animeMetaModel.releasedDate?.let {
            holder.releasedDate.text = it
        }
        holder.root.setOnClickListener(clickListener)
        //Set Shared Element for Anime Title
        var animeTitleTransition = holder.root.context.getString(R.string.shared_anime_title)
        animeTitleTransition =
            "${animeTitleTransition}_${animeMetaModel.title}_${animeMetaModel.ID}"
        holder.animeTitle.transitionName = animeTitleTransition

        //Set Shared Element for Anime Image
        var animeImageTransition = holder.root.context.getString(R.string.shared_anime_image)
        animeImageTransition =
            "${animeImageTransition}_${animeMetaModel.imageUrl}_${animeMetaModel.ID}"
        holder.animeImageView.transitionName = animeImageTransition

    }

    class MovieHolder : EpoxyHolder() {

        lateinit var animeImageView: AppCompatImageView
        lateinit var animeTitle: TextView
        lateinit var releasedDate: TextView
        lateinit var root: ConstraintLayout

        override fun bindView(itemView: View) {
            animeImageView = itemView.findViewById(R.id.animeImage)
            animeTitle = itemView.findViewById(R.id.animeTitle)
            releasedDate = itemView.findViewById(R.id.releasedDate)
            root = itemView.findViewById(R.id.root)
        }

    }
}

@EpoxyModelClass(layout = R.layout.recycler_loading)
abstract class LoadingModel : EpoxyModelWithHolder<LoadingHolder>()

class LoadingHolder : EpoxyHolder() {
    override fun bindView(itemView: View) {
    }
}

