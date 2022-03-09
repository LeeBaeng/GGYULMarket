package com.leebaeng.ggyulmarket

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.color.MaterialColors
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
import com.leebaeng.ggyulmarket.common.constants.DBKey
import com.leebaeng.ggyulmarket.common.ext.dpToPx
import com.leebaeng.ggyulmarket.databinding.ActivityAddMarketItemBinding
import com.leebaeng.ggyulmarket.home.MarketModel
import com.leebaeng.util.log.LLog
import gun0912.tedbottompicker.TedBottomPicker


class AddMarketItemActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddMarketItemBinding
    private var onCompleteBtnClickListener = View.OnClickListener { reqAddMarketItem() }
    private var onBackBtnClickListener = View.OnClickListener { Toast.makeText(binding.root.context, "on BackBtn Click!", Toast.LENGTH_SHORT).show() }
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
        binding = ActivityAddMarketItemBinding.inflate(layoutInflater)
        binding.onCompleteBtnClickListener = onCompleteBtnClickListener
        binding.onBackBtnClickListener = onBackBtnClickListener
        setContentView(binding.root)

//        initImagePickerBySelf()
        initImagePickerByTedBottomPicker()
    }

    fun reqAddMarketItem() {
        Toast.makeText(binding.root.context, "on CompleteBtn Click!", Toast.LENGTH_SHORT).show()
        val title = binding.edtTitle.text.toString()
        val price = binding.edtPrice.text.toString().toInt()
        val description = binding.edtDescription.text.toString()
        val sellerId = auth.currentUser?.uid.orEmpty()
        val imgUrlList = mutableListOf<String>().apply {

        }

//        val sellerId: String = "",
//        val title: String = "",
//        val createdAt: Long = 0L,
//        val price: Int = 0,
//        val imgUrl: String? = null,
//        val talkCnt: Int? = null,
//        val likeCnt: Int? = null,
//        val id: String = "$sellerId-$createdAt"
        val model = MarketModel(sellerId, title, System.currentTimeMillis(), price, description, imgUrlList)

        marketListDB.push().setValue(model)

        finish()
    }


    // region ===========InitImagePicker===========
    fun initImagePickerByTedBottomPicker() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@AddMarketItemActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(this@AddMarketItemActivity, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check();

        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
//        val color: Int = MaterialColors.getColor(context, R.attr.colorPrimary, Color.BLACK)

        // TedBottomPicker 라이브러리 이지미 피커 사용
        binding.layoutBtnAddImg.setOnClickListener {
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
                    list.forEach { addLoadedImage(it) }
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


}