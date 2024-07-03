package com.qureka.skool.common

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.qureka.skool.utils.Utils
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class AppSignatureHelper(private val base: Context?) : ContextWrapper(base) {
    val appSign =
        "3082058930820371a003020102021500c973f6d6d2b7f0c1b102bb897876a41391d78632300d06092a864886f70d01010b05003074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f69643020170d3231313130323036353730305a180f32303531313130323036353730305a3074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f696430820222300d06092a864886f70d01010105000382020f003082020a0282020100cb3967cef36f3d3f93729e1c22d786df86efafbdeb3a36dbb9eef49480bbcc55fc9ef34ece90768350c75922c74fa9c7f9bc181271e0fa0ca240bffb38d048d0fece62a393ab8de2a4b322fa3781bb8d12bb98016496c258a3a7540610515d07ecf93d6893053bdac8a298810f8f1f607937416e65148466e1ae5bb5e7ae230a2a5fece5d4171ff1d211b3771dce92d5a8d147f72994b6261106ed063df26a79eb95bbbfd0a39d3c6b016fa17923661b2abd9fa30660ac040f0963961bc5b26f6564d58ff31e4c8ce99a0fa20ec979b25d35e944eae0fc02dee65e2f78e7688aa32bdeb5122dbf77fde6ef2811ffe9902225ab206f46744f317c9f58bf4b48451853f834a274caf890e0ed72d6de09626da79d3cfe9de826318668e594d5685ef70e2027fd47ec3e1c6112308daff48083d59d9843845fca6d6c715e2ae370fc874be67c2ca3f6a23d3431068a4d5d27b99e02edbfff5c89ebdebe0b94f2dce7f4a7d5807d8d8a01768eeeaf35532877e699ad6bb201da4dc313b3e3d0a851a50fb7ed4d57cdae67b44d6b533d5578c7571794b80b917081d1a1e4fb0da72035d74ea18c16cfcb16b4f583755e7f1a06b4b0e701e5d40c89ddaa9f4576b0198daf9cc9e064d01795a431351a0373d74e4fbf23bd312a782b333510a00265a7bb3577cd34739a18e2369572a141282d5272305d0b71695366787bb107b209ffc30203010001a310300e300c0603551d13040530030101ff300d06092a864886f70d01010b0500038202010025b04fd18849b4e790504b58f190f917e9b941922b03f56f026ce51c1143e76bd4de1b82b9d244e517fcd6093ec8a2fb6b87a2e07b7443416ff81a0b7a6d8a8f555898ce4f54d0964c15e6e6fbc90cba5d11afb156f60602be860f0560b9d8b6a0789e057e3a85b5b22da5b6c8f6217d222e588c4ff76196559e7e961d250eecfac548386d12a956c906135b4e86809fe21c0b564a08be51fe50fe2a5b06651562ba5133ef7f7436314d36fae8a3d94853a60349cef8074e5f8b3dfc76807fa67719b2fb0c372833df0bd333d172feb91497733b6bfef96935386217c2d54fadaff0c71a790c1702193c86af096d08975b780dfa8bbd597935a19dc4350ada575c7573685f288307b60e0f2dd8d80f3a3775537f17920d10b44d686901519d38710057a142dcd1b9e31ec303815f50cdd0c5b1b8764a6d4e1743f86baf748e7181aefd4eed7ce2412122f20edeb928dd873c079bbbe044d312bcf03fa012203e70b342ae2a28e6a62c67278386c6c08ee181e6a3764b6a665a8a7b1ed9e2b286fe7b820264ed553156be3402ac2b330234b8c7a7438c516cf8ec1e798786c75e73f360646cda5889a52201c7cadb693c648a2a74f769728aa8c8efe69fcfb21b8fc97620ec842c40a453a5e7f51aea5bdcf401928c733758fbf4cafc5adffa5d3bb84151a77e958578fee8436111cb6ad7758369ae8d496d0fde4da619bb63dc"
    val TAG = AppSignatureHelper::class.java.simpleName

    private val HASH_TYPE = "SHA-256"
    val NUM_HASHED_BYTES = 9
    val NUM_BASE64_CHAR = 11

    /**
     * Get all the app signatures for the current package
     *
     * @return
     */
    fun getAppSignatures(): ArrayList<String> {
        val appCodes = ArrayList<String>()

        try {
            // Get all package signatures for the current package
            val packageName = packageName
            val packageManager = packageManager
            val signatures = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures

            // For each signature create a compatible hash
            for (signature in signatures) {
                val hash = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(String.format("%s", hash))
                }
                if (base != null) {
                    Utils.showToast(context = base, hash?:"N/A")
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Unable to find package to obtain hash.", e)
        } catch (e: NoClassDefFoundError) {

        }

        return appCodes
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
            // encode into Base64
            var base64Hash =
                Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

            println(String.format("pkg: %s -- hash: %s", packageName, base64Hash))
            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "hash:NoSuchAlgorithm", e)
        }

        return null
    }

}