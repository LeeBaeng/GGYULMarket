package com.leebaeng.ggyulmarket.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.ext.*
import com.leebaeng.ggyulmarket.databinding.ActivityAddMarketItemBinding
import com.leebaeng.ggyulmarket.di.DBAdapter
import com.leebaeng.ggyulmarket.model.MarketModel
import com.leebaeng.util.log.*
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddMarketItemActivity : BaseActivity() {
    @Inject lateinit var dbAdapter: DBAdapter
    lateinit var binding: ActivityAddMarketItemBinding
    private var onCompleteBtnClickListener = View.OnClickListener { checkAddMarketItem() }
    private var onBackBtnClickListener = View.OnClickListener { finish() }
    private var selectedImgUri = mutableListOf<Uri>()

//    private val auth: FirebaseAuth by lazy {
//        Firebase.auth
//    }

//    private val marketListRef: CollectionReference by lazy {
//        Firebase.firestore.collection(Const.FS_TABLE_MARKET)
//    }
//
//    private val storage: FirebaseStorage by lazy {
//        Firebase.storage
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMarketItemBinding.inflate(layoutInflater)
        binding.onCompleteBtnClickListener = onCompleteBtnClickListener
        binding.onBackBtnClickListener = onBackBtnClickListener
        binding.edtPrice.setPriceCommaFormat()
        setContentView(binding.root)

//        initImagePickerBySelf()
        initImagePickerByTedBottomPicker()
        "this is log".logW()
    }

    fun checkAddMarketItem() {
        fun showToastFail(msg: String) = msg.showLongToast(binding.root.context)

        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, 0)
//        if (auth.currentUser == null) {
//            showToastFail("????????? ??? ????????? ?????? ?????????."); return
//        }
        if (binding.edtTitle.text.isNullOrEmpty()) {
            showToastFail("????????? ????????? ?????????"); return
        }
        if (binding.edtPrice.text.isNullOrEmpty()) {
            showToastFail("????????? ????????? ?????????"); return
        }
        if (binding.edtDescription.text.isNullOrEmpty()) {
            showToastFail("?????? ????????? ????????? ?????????"); return
        }

        val title = binding.edtTitle.text.toString()
        val price = binding.edtPrice.text?.toString()?.toLongRemovedComma() ?: 0
        val description = binding.edtDescription.text.toString()
        val isPriceProposeAble = binding.chkProposePrice.isChecked
        val sellerId = dbAdapter.auth.currentUser?.uid.orEmpty()

        binding.layoutLoading.isVisible = true

        CoroutineScope(Dispatchers.IO).launch {
            var imgUrlList: MutableList<String>? = null
            uploadPhotoMultiple(sellerId, selectedImgUri) { remoteUrlList, uploadFailedList ->
                binding.root.post { binding.layoutLoading.isVisible = false }
                if (!remoteUrlList.isNullOrEmpty()) imgUrlList = remoteUrlList
                if (!uploadFailedList.isNullOrEmpty()) {
                    val sb = StringBuffer()
                    uploadFailedList.forEach { sb.append(it.exception) }
                    binding.root.post {
                        showToastFail("${uploadFailedList.size}?????? ????????? ????????????.\n${sb}")
                    }
                } else {
                    reqPushToDB(title, price, description, sellerId, isPriceProposeAble, imgUrlList)
                }
            }
        }
    }

    /**
     * ????????? ????????? ?????? Remote Url ????????? ????????????.
     */
    fun uploadPhoto(fileName: String, localUri: Uri, callback: (UploadPhotoResult) -> Unit) {
        val storageRef = dbAdapter.marketPhotoRef.child(fileName)
        storageRef
            .putFile(localUri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { remoteUri ->
                        callback(UploadPhotoResult(true, remoteUri.toString()))
                    }.addOnFailureListener { e ->
                        callback(UploadPhotoResult(false, exception = e))
                    }
                } else {
                    callback(UploadPhotoResult(false, exception = it.exception))
                }
            }
    }

    fun uploadPhotoMultiple(sellerId: String, imgList: MutableList<Uri>, callback: (MutableList<String>?, MutableList<UploadPhotoResult>) -> Unit) {
        val imgUrlList = mutableListOf<String>()
        val uploadFailedList = mutableListOf<UploadPhotoResult>()
        val completedList = mutableListOf<UploadPhotoResult>()

        if (imgList.isNotEmpty()) {
            imgList.forEachIndexed { index, uri ->
                val fileName = "${sellerId}_${System.currentTimeMillis()}_$index.png"
                uploadPhoto(fileName, uri) { rst ->
                    completedList.add(rst)
                    if (rst.isSuccess && rst.remoteUrl != null) imgUrlList.add(rst.remoteUrl)
                    else uploadFailedList.add(rst)

                    if (completedList.size == imgList.size) {
                        callback(imgUrlList, uploadFailedList)
                    }
                }
            }
        } else callback(imgUrlList, uploadFailedList)
    }

    fun reqPushToDB(title: String, price: Long, description: String, sellerId: String, isPriceProposeAble: Boolean, imgUrlList: MutableList<String>?) {
        val model = MarketModel(sellerId, title, System.currentTimeMillis(), price, description, isPriceProposeAble, imgUrlList)
        val marketListRef = dbAdapter.marketDB
        marketListRef.document(model.id)
            .set(model)
            .addOnSuccessListener {
                "DocumentSnapshot added with ID: ${model.id}".logD()
                "??????????????? ?????? ???????????????.".showShortToast(this)
                setResult(1000)
                finish()
            }
            .addOnFailureListener { e ->
                "????????? ?????? ????????????.".showShortToast(this)
                e.logEX("Error adding document")
            }

//        marketListDB.push().setValue(MarketModel(sellerId, title, System.currentTimeMillis(), price, description, imgUrlList)).addOnSuccessListener {
//            "??????????????? ?????? ???????????????.".showShortToast(this)
//            finish()
//        }.addOnFailureListener {
//            "????????? ?????? ????????????.".showShortToast(this)
//        }
    }

    // region ===========InitImagePicker===========
    fun initImagePickerByTedBottomPicker() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}
            override fun onPermissionDenied(deniedPermissions: List<String>) {
                "$deniedPermissions ????????? ???????????? ????????? ?????? ??? ????????????.".showLongToast(binding.root.context)
            }
        }

        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
//        val color: Int = MaterialColors.getColor(context, R.attr.colorPrimary, Color.BLACK)

        // TedBottomPicker ??????????????? ????????? ?????? ??????
        binding.layoutBtnAddImg.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@AddMarketItemActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                "${Manifest.permission.WRITE_EXTERNAL_STORAGE} ????????? ???????????? ????????? ?????? ??? ????????????.".showLongToast(this@AddMarketItemActivity)
                return@setOnClickListener
            }
            TedBottomPicker.with(this)
                .setPeekHeight(1600)
                .showTitle(false)
                .setSpacing(10)
                .setSelectMaxCount(10)
                .setCompleteButtonText("??????")
                .setEmptySelectionText("????????? ???????????? ????????????.")
                .setCameraTileBackgroundResId(typedValue.data)
                .setGalleryTileBackgroundResId(typedValue.data)
                .setTitleBackgroundResId(R.color.red)
                .showMultiImage { list ->
                    // here is selected image uri list
                    list.forEach {
                        addLoadedImage(it)
                        selectedImgUri.add(it)
                    }
                }
        }
    }

    fun initImagePickerBySelf() {
        // android registerForActivityResult & launch ???????????? ????????? ?????? ??????
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            Toast.makeText(this, "$uri", Toast.LENGTH_SHORT).show()
            uri?.let { addLoadedImage(uri) }
        }
        binding.layoutBtnAddImg.setOnClickListener { getContent.launch("image/*") }
    }

    fun addLoadedImage(uri: Uri) {
        LLog.warn("add loadImage $uri")
        val layout = layoutInflater.inflate(R.layout.item_img_list, binding.layoutImgList, false)
        binding.layoutImgList.addView(layout)
        Glide.with(this)
            .load(uri)
            .transform(CenterCrop(), RoundedCorners(10f.dpToPx(binding.root.context)))
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    binding.scrollImgList.fullScroll(View.FOCUS_RIGHT)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean = false
            })
            .into(layout.findViewById(R.id.imgThumbnail))
    }
    // endregion


    data class UploadPhotoResult(val isSuccess: Boolean, val remoteUrl: String? = null, val exception: Exception? = null)
}