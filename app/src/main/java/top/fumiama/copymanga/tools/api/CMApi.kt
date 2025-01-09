package top.fumiama.copymanga.tools.api

import androidx.preference.PreferenceManager
import com.bumptech.glide.load.model.LazyHeaders
import top.fumiama.copymanga.MainActivity
import top.fumiama.copymanga.tools.http.DownloadTools
import top.fumiama.copymanga.tools.http.Proxy
import top.fumiama.copymanga.tools.http.Resolution
import top.fumiama.dmzj.copymanga.R
import java.io.File

object CMApi {
    var imageProxy: Proxy? = null
        get() {
            if (field != null) return field
            if (Proxy.useImageProxy) field = Proxy(
                R.string.imgProxyApiUrl,
                Regex("^https://[0-9a-z-]+\\.mangafun[a-z]\\.(xyz|fun)/"),
                R.string.imgProxyKeyID
            )
            return field
        }
    var apiProxy: Proxy? = null
        get() {
            if (field != null) return field
            if (Proxy.useApiProxy) field = Proxy(
                R.string.apiProxyApiUrl,
                Regex("^https://api\\.(copymanga|mangacopy)\\.\\w+/api/"),
                R.string.imgProxyKeyID
            )
            return field
        }
    var resolution = Resolution(Regex("c\\d+x\\."))
    var myGlideHeaders: LazyHeaders? = null
        get() {
            MainActivity.mainWeakReference?.get()?.let {
                PreferenceManager.getDefaultSharedPreferences(it).apply {
                    if (field === null)
                        field = LazyHeaders.Builder()
                            .addHeader("referer", DownloadTools.referer)
                            .addHeader("User-Agent", DownloadTools.pc_ua)
                            .addHeader("source", "copyApp")
                            .addHeader("webp", "1")
                            .addHeader("version", DownloadTools.app_ver)
                            .addHeader(
                                "region",
                                if (!getBoolean("settings_cat_net", false)) "1" else "0"
                            )
                            .addHeader("platform", "3")
                            .build()
                }
            }
            return field
        }
    var myHostApiUrl: String = ""
        get() {
            if (field != "") return field
            MainActivity.mainWeakReference?.get()?.let {
                PreferenceManager.getDefaultSharedPreferences(it).apply {
                    getString("settings_cat_net_et_api_url", "")?.let { host ->
                        if (host != "") {
                            field = host
                            return host
                        }
                    }
                }
                field = it.getString(R.string.hostUrl)
            }
            return field
        }

    fun getZipFile(exDir: File?, manga: String, caption: CharSequence, name: CharSequence) =
        File(exDir, "$manga/$caption/$name.zip")

    fun getChapterInfoApiUrl(path: String?, uuid: String?, version: Int) =
        MainActivity.mainWeakReference?.get()?.getString(R.string.chapterInfoApiUrl)
            ?.format(myHostApiUrl, path, if (version >= 2) "$version" else "" , uuid)
}
