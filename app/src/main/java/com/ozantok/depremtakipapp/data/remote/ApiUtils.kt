package com.ozantok.depremtakipapp.data.remote
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ApiUtils {
    class EarthquakeResponseBodyConverter : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *>? {
            // AFAD API'sinden dönen JSON formatını uygun formata dönüştürmek için
            // özel bir converter sağlayabiliriz
            return if (type == String::class.java) {
                Converter<ResponseBody, String> { body -> body.string() }
            } else {
                super.responseBodyConverter(type, annotations, retrofit)
            }
        }
    }
}