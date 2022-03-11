package com.leebaeng.ggyulmarket.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.constants.DBKey
import com.leebaeng.ggyulmarket.common.ext.*
import com.leebaeng.ggyulmarket.databinding.ActivityAddMarketItemBinding
import com.leebaeng.ggyulmarket.model.MarketModel
import com.leebaeng.util.log.LLog
import com.leebaeng.util.log.logW
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.bumptech.glide.request.target.Target
import com.leebaeng.util.log.logS

class AddMarketItemActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddMarketItemBinding
    private var onCompleteBtnClickListener = View.OnClickListener { checkAddMarketItem() }
    private var onBackBtnClickListener = View.OnClickListener { finish() }
    private var selectedImgUri = mutableListOf<Uri>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val marketListDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.TABLE_MARKET_LIST)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "onCreate AddMarketItemActivity!!".logS()

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

//        if (auth.currentUser == null) {
//            showToastFail("로그인 후 등록이 가능 합니다."); return
//        }
        if (binding.edtTitle.text.isNullOrEmpty()) {
            showToastFail("제목을 입력해 주세요"); return
        }
        if (binding.edtPrice.text.isNullOrEmpty()) {
            showToastFail("가격을 입력해 주세요"); return
        }
        if (binding.edtDescription.text.isNullOrEmpty()) {
            showToastFail("상세 내용을 입력해 주세요"); return
        }

        val title = binding.edtTitle.text.toString()
        val price = binding.edtPrice.text?.toString()?.toLongRemovedComma() ?: 0
        val description = binding.edtDescription.text.toString()
        val sellerId = auth.currentUser?.uid.orEmpty()
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
                        showToastFail("${uploadFailedList.size}건의 실패가 있습니다.\n${sb}")
                    }
                }else{
                    reqPushToDB(title, price, description, sellerId, imgUrlList)
                }
            }
        }
    }

    /**
     * 사진을 업로드 하고 Remote Url 주소를 반환한다.
     */
    fun uploadPhoto(fileName: String, localUri: Uri, callback: (UploadPhotoResult) -> Unit) {
        val storageRef = storage.reference.child("market/photo").child(fileName)
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

    fun reqPushToDB(title: String, price: Long, description: String, sellerId: String, imgUrlList: MutableList<String>?) {
        marketListDB.push().setValue(MarketModel(sellerId, title, System.currentTimeMillis(), price, description, imgUrlList)).addOnSuccessListener {
            "성공적으로 등록 되었습니다.".showShortToast(this)
            finish()
        }.addOnFailureListener {
            "등록에 실패 했습니다.".showShortToast(this)
        }
    }

    // region ===========InitImagePicker===========
    fun initImagePickerByTedBottomPicker() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}
            override fun onPermissionDenied(deniedPermissions: List<String>) {
                "$deniedPermissions 권한이 거부되어 사진을 올릴 수 없습니다.".showLongToast(binding.root.context)
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

        // TedBottomPicker 라이브러리 이지미 피커 사용
        binding.layoutBtnAddImg.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this@AddMarketItemActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                "${Manifest.permission.WRITE_EXTERNAL_STORAGE} 권한이 거부되어 사진을 올릴 수 없습니다.".showLongToast(this@AddMarketItemActivity)
                return@setOnClickListener
            }
            TedBottomPicker.with(this)
                .setPeekHeight(1600)
                .showTitle(false)
                .setSpacing(10)
                .setSelectMaxCount(10)
                .setCompleteButtonText("완료")
                .setEmptySelectionText("선택된 이미지가 없습니다.")
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
        // android registerForActivityResult & launch 방식으로 이미지 피커 사용
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