package com.arosara.anisaw.ui.main.home.epoxy

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.arosara.anisaw.R
import com.arosara.anisaw.utils.Tags.GenreTags
import com.arosara.anisaw.utils.model.AnimeMetaModel
import org.apmem.tools.layouts.FlowLayout


@EpoxyModelClass(layout = R.layout.recycler_anime_recent_sub_dub_2)
abstract class AnimeSubDubModel2 : EpoxyModelWithHolder<AnimeSubDubModel2.SubDubHolder>() {

    @EpoxyAttribute
    lateinit var animeMetaModel: AnimeMetaModel

    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: SubDubHolder) {
        Glide.with(holder.animeImageView.context).load(animeMetaModel.imageUrl)
            .into(holder.animeImageView)
        holder.animeTitle.text = animeMetaModel.title
        holder.animeEpisode.text = animeMetaModel.episodeNumber
        holder.animeImageView.setOnClickListener(clickListener)
        holder.animeTitle.setOnClickListener(clickListener)

        //Set Shared Element for Anime Title
        var animeTitleTransition = holder.animeTitle.context.getString(R.string.shared_anime_title)
        animeTitleTransition =
            "${animeTitleTransition}_${animeMetaModel.title}_${animeMetaModel.ID}"
        holder.animeTitle.transitionName = animeTitleTransition

        //Set Shared Element for Anime Image
        var animeImageTransition = holder.animeImageView.context.getString(R.string.shared_anime_image)
        animeImageTransition =
            "${animeImageTransition}_${animeMetaModel.imageUrl}_${animeMetaModel.ID}"
        holder.animeImageView.transitionName = animeImageTransition

    }

    class SubDubHolder : EpoxyHolder() {

        lateinit var animeImageView: AppCompatImageView
        lateinit var animeCardView: CardView
        lateinit var animeTitle: TextView
        lateinit var animeEpisode: TextView

        override fun bindView(itemView: View) {
            animeImageView = itemView.findViewById(R.id.animeImage)
            animeCardView = itemView.findViewById(R.id.animeCardView)
            animeTitle = itemView.findViewById(R.id.animeTitle)
            animeEpisode = itemView.findViewById(R.id.episodeNumber)
        }

    }
}

@EpoxyModelClass(layout = R.layout.recycler_anime_popular)
abstract class AnimePopularModel : EpoxyModelWithHolder<AnimePopularModel.PopularHolder>() {

    @EpoxyAttribute
    lateinit var animeMetaModel: AnimeMetaModel

    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: PopularHolder) {
        Glide.with(holder.animeImageView.context).load(animeMetaModel.imageUrl).centerCrop()
            .into(holder.animeImageView)
        holder.animeTitle.text = animeMetaModel.title
        holder.animeEpisode.text = animeMetaModel.episodeNumber

        holder.flowLayout.removeAllViews()

        animeMetaModel.genreList?.forEach {
            holder.flowLayout.addView(
                GenreTags(holder.flowLayout.context).getGenreTag(
                    it.genreName,
                    it.genreUrl
                )
            )
        }
        holder.rootView.setOnClickListener(clickListener)

        //Set Shared Element for Anime Title
        var animeTitleTransition = holder.rootView.context.getString(R.string.shared_anime_title)
        animeTitleTransition =
            "${animeTitleTransition}_${animeMetaModel.title}_${animeMetaModel.ID}"
        holder.animeTitle.transitionName = animeTitleTransition

        //Set Shared Element for Anime Image
        var animeImageTransition = holder.rootView.context.getString(R.string.shared_anime_image)
        animeImageTransition =
            "${animeImageTransition}_${animeMetaModel.imageUrl}_${animeMetaModel.ID}"
        holder.animeImageView.transitionName = animeImageTransition

    }

    class PopularHolder : EpoxyHolder() {

        lateinit var animeImageView: AppCompatImageView
        lateinit var animeTitle: TextView
        lateinit var animeEpisode: TextView
        lateinit var flowLayout: FlowLayout
        lateinit var rootView: ConstraintLayout
        lateinit var cardView: CardView

        override fun bindView(itemView: View) {
            animeImageView = itemView.findViewById(R.id.animeImage)
            animeTitle = itemView.findViewById(R.id.animeTitle)
            animeEpisode = itemView.findViewById(R.id.episodeNumber)
            flowLayout = itemView.findViewById(R.id.flowLayout)
            rootView = itemView.findViewById(R.id.rootLayout)
            cardView = itemView.findViewById(R.id.animeCardView)
        }

    }
}


@EpoxyModelClass(layout = R.layout.recycler_anime_mini_header)
abstract class AnimeMiniHeaderModel :
    EpoxyModelWithHolder<AnimeMiniHeaderModel.AnimeMiniHeaderHolder>() {

    @EpoxyAttribute
    lateinit var typeName: String

    override fun bind(holder: AnimeMiniHeaderHolder) {
        super.bind(holder)
        holder.animeType.text = typeName
    }


    class AnimeMiniHeaderHolder : EpoxyHolder() {

        lateinit var animeType: TextView

        override fun bindView(itemView: View) {
            animeType = itemView.findViewById(R.id.typeName)
        }

    }

}


