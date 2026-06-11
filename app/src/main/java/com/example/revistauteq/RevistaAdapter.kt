package com.example.revistauteq

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

private const val PORTADA_BASE_URL = "https://uteq.edu.ec/assets/images/newspapers/"

class RevistaAdapter(
    context: Context,
    var revistas: ArrayList<Revista>
) : ArrayAdapter<Revista>(context, R.layout.item_revista, revistas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_revista, parent, false)

        val revista = revistas[position]

        val ivPortada = view.findViewById<ImageView>(R.id.ivPortada)
        val tvAnioMes = view.findViewById<TextView>(R.id.tvAnioMes)
        val tvUrlPdf = view.findViewById<TextView>(R.id.tvUrlPdf)

        tvAnioMes.text = "Año: ${revista.anio}   Mes: ${revista.mes}"

        val spannable = SpannableString(revista.urlpw)
        spannable.setSpan(UnderlineSpan(), 0, revista.urlpw.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvUrlPdf.text = spannable

        Glide.with(context)
            .load(PORTADA_BASE_URL + revista.urlportada)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(ivPortada)

        val openPdf = View.OnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(revista.urlpw))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        view.setOnClickListener(openPdf)
        tvUrlPdf.setOnClickListener(openPdf)

        return view
    }
}
