package fr.free.nrw.commons.upload.license

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import fr.free.nrw.commons.R
import fr.free.nrw.commons.Utils
import fr.free.nrw.commons.databinding.FragmentMediaLicenseBinding
import fr.free.nrw.commons.upload.UploadActivity
import fr.free.nrw.commons.upload.UploadBaseFragment
import fr.free.nrw.commons.utils.DialogUtil.showAlertDialog
import timber.log.Timber
import javax.inject.Inject

class MediaLicenseFragment : UploadBaseFragment(), MediaLicenseContract.View {
    @JvmField
    @Inject
    var presenter: MediaLicenseContract.UserActionListener? = null

    private var binding: FragmentMediaLicenseBinding? = null
    private var adapter: ArrayAdapter<String>? = null
    private var licenses: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaLicenseBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.tvTitle.text = getString(
            R.string.step_count,
            callback.getIndexInViewFlipper(this) + 1,
            callback.totalNumberOfSteps,
            getString(R.string.license_step_title)
        )
        setTvSubTitle()
        binding!!.btnPrevious.setOnClickListener { v: View? ->
            callback.onPreviousButtonClicked(
                callback.getIndexInViewFlipper(this)
            )
        }

        binding!!.btnSubmit.setOnClickListener { v: View? ->
            callback.onNextButtonClicked(
                callback.getIndexInViewFlipper(this)
            )
        }

        binding!!.tooltip.setOnClickListener { v: View? ->
            showAlertDialog(
                requireActivity(),
                getString(R.string.license_step_title),
                getString(R.string.license_tooltip),
                getString(android.R.string.ok),
                null
            )
        }

        initPresenter()
        initLicenseSpinner()
        presenter!!.getLicenses()
    }

    /**
     * Removes the tv Subtitle If the activity is the instance of [UploadActivity] and
     * if multiple files aren't selected.
     */
    private fun setTvSubTitle() {
        val activity: Activity? = activity
        if (activity is UploadActivity) {
            val isMultipleFileSelected = activity.isMultipleFilesSelected
            if (!isMultipleFileSelected) {
                binding!!.tvSubtitle.visibility = View.GONE
            }
        }
    }

    private fun initPresenter() {
        presenter!!.onAttachView(this)
    }

    /**
     * Initialise the license spinner
     */
    private fun initLicenseSpinner() {
        if (activity == null) {
            return
        }
        adapter = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_dropdown_item
        )
        binding!!.spinnerLicenseList.adapter = adapter
        binding!!.spinnerLicenseList.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>, view: View, position: Int,
                    l: Long
                ) {
                    val licenseName = adapterView.getItemAtPosition(position).toString()
                    presenter!!.selectLicense(licenseName)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    presenter!!.selectLicense(null)
                }
            }
    }

    override fun setLicenses(licenses: List<String>?) {
        adapter!!.clear()
        this.licenses = licenses
        adapter!!.addAll(this.licenses!!)
        adapter!!.notifyDataSetChanged()
    }

    override fun setSelectedLicense(license: String?) {
        var position = licenses!!.indexOf(getString(Utils.licenseNameFor(license)))
        // Check if position is valid
        if (position < 0) {
            Timber.d("Invalid position: %d. Using default licenses", position)
            position = licenses!!.size - 1
        } else {
            Timber.d("Position: %d %s", position, getString(Utils.licenseNameFor(license)))
        }
        binding!!.spinnerLicenseList.setSelection(position)
    }

    override fun updateLicenseSummary(selectedLicense: String?, numberOfItems: Int) {
        val licenseHyperLink = "<a href='" + Utils.licenseUrlFor(selectedLicense) + "'>" +
                getString(Utils.licenseNameFor(selectedLicense)) + "</a><br>"

        setTextViewHTML(
            binding!!.tvShareLicenseSummary, resources
                .getQuantityString(
                    R.plurals.share_license_summary, numberOfItems,
                    licenseHyperLink
                )
        )
    }

    private fun setTextViewHTML(textView: TextView, text: String) {
        val sequence: CharSequence = Html.fromHtml(text)
        val strBuilder = SpannableStringBuilder(sequence)
        val urls = strBuilder.getSpans(
            0, sequence.length,
            URLSpan::class.java
        )
        for (span in urls) {
            makeLinkClickable(strBuilder, span)
        }
        textView.text = strBuilder
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun makeLinkClickable(strBuilder: SpannableStringBuilder, span: URLSpan) {
        val start = strBuilder.getSpanStart(span)
        val end = strBuilder.getSpanEnd(span)
        val flags = strBuilder.getSpanFlags(span)
        val clickable: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Handle hyperlink click
                val hyperLink = span.url
                launchBrowser(hyperLink)
            }
        }
        strBuilder.setSpan(clickable, start, end, flags)
        strBuilder.removeSpan(span)
    }

    private fun launchBrowser(hyperLink: String) {
        Utils.handleWebUrl(context, Uri.parse(hyperLink))
    }

    override fun onDestroyView() {
        presenter!!.onDetachView()
        //Free the adapter to avoid memory leaks
        adapter = null
        binding = null
        super.onDestroyView()
    }

    override fun onBecameVisible() {
        super.onBecameVisible()
        /**
         * Show the wlm info message if the upload is a WLM upload
         */
        if (callback.isWLMUpload && presenter!!.isWLMSupportedForThisPlace()) {
            binding!!.llInfoMonumentUpload.visibility = View.VISIBLE
        } else {
            binding!!.llInfoMonumentUpload.visibility = View.GONE
        }
    }
}
