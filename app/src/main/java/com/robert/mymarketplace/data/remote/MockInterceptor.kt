package com.robert.mymarketplace.data.remote

import com.robert.mymarketplace.data.ApiConstants.API_SUCCESS_CODE
import com.robert.mymarketplace.data.ApiConstants.BASE_IMAGE_URL
import com.robert.mymarketplace.data.ApiConstants.CONTENT_TYPE
import com.robert.mymarketplace.data.ApiConstants.CONTENT_TYPE_VALUE
import com.robert.mymarketplace.data.ApiConstants.METHOD_GET
import com.robert.mymarketplace.data.ApiConstants.METHOD_POST
import com.robert.mymarketplace.data.ApiConstants.METHOD_PUT
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url.toUri()
        val path = uri.path
        val method = chain.request().method

        val responseString = when {
            path.endsWith("listings") && method == METHOD_GET -> {
                val titles = listOf(
                    "Prime 5-Acre Residential Plot",
                    "Riverside Agricultural Farmland",
                    "Strategic Commercial Corner Lot",
                    "Luxury Hilltop Development Land",
                    "Serene Forest Retreat Acreage",
                    "Industrial Zone Warehouse Plot",
                    "Beachfront Development Opportunity",
                    "Suburban Infill Lot with Utilities",
                    "Mountain View Ranch Land",
                    "Eco-Friendly Orchard Estate",
                    "Downtown Redevelopment Site",
                    "Gated Community Luxury Plot",
                    "Fertile Vineyard Expansion Land",
                    "Highway-Frontage Commercial Land",
                    "Quiet Cul-de-Sac Home Site",
                    "Lakeside Recreational Acreage",
                    "Sunset Ridge Residential Parcel",
                    "Greenbelt-Adjacent Family Lot",
                    "Boutique Hotel Development Site",
                    "Expansive Grazing Pasture"
                )

                val descriptions = listOf(
                    "Spacious 5-acre plot located in a fast-growing residential area. Flat topography, ready for immediate construction with electricity and water connections already on-site. Perfect for a luxury estate or small development.",
                    "Highly fertile land situated along the perennial riverbanks. Ideal for high-yield seasonal crops or perennial orchards. Includes existing irrigation infrastructure and easy access to local markets.",
                    "High-visibility corner lot at a busy intersection. Zoned for commercial use, including retail, office space, or a service station. Excellent foot traffic and vehicle accessibility guaranteed.",
                    "Exclusive hilltop land offering panoramic views of the city skyline. Zoned for luxury residential development. Features a private access road and is surrounded by high-end completed properties.",
                    "Tranquil acreage nestled within a protected forest area. Ideal for a private getaway, eco-resort, or sustainable cabin living. Experience absolute privacy and a direct connection with nature.",
                    "Large flat parcel located within a designated industrial park. Direct heavy-vehicle access, high-capacity power lines, and proximity to major rail links. Suitable for manufacturing or logistics hubs.",
                    "Rare beachfront property with direct access to pristine white sands. Perfect for a boutique hotel, luxury villas, or an upscale restaurant. High tourism potential in an emerging coastal destination.",
                    "Ready-to-build lot in an established suburban neighborhood. Paved road access, high-speed internet available, and close proximity to top-rated schools and shopping centers.",
                    "Ruggedly beautiful land with unobstructed views of the surrounding peaks. Ideal for a mountain ranch, retreat center, or adventure tourism base. Includes natural spring water rights.",
                    "Productive orchard land featuring mature fruit trees and organic soil certification. Includes a small storage facility and a solar-powered well. Perfect for sustainable agricultural ventures.",
                    "Premium site in the heart of the city, cleared and ready for high-density mixed-use development. Proximity to transit hubs and major corporate offices ensures high ROI.",
                    "Secure plot within a premier gated community. Features 24/7 security, landscaped surroundings, and access to communal clubhouses and sports facilities. Build your dream family home.",
                    "Stunning parcel with ideal micro-climate for viticulture. Rolling hills with well-drained soil, perfect for expanding an existing winery or starting a boutique vineyard.",
                    "Prime land with extensive frontage on the main highway. Perfect for a truck stop, distribution warehouse, or large-scale retail center. Unbeatable visibility for passing traffic.",
                    "Peaceful residential lot located at the end of a quiet cul-de-sac. Minimal traffic, safe for children, and surrounded by mature trees. A perfect sanctuary for a family home.",
                    "Beautiful acreage bordering a large recreational lake. Features private jetty potential and is zoned for seasonal residences or holiday camps. Excellent fishing and boating access.",
                    "Elevated parcel offering stunning sunset views every evening. Located in a premium residential zone with modern infrastructure and underground utility lines already installed.",
                    "Large corner lot bordering a protected greenbelt area. Enjoy permanent views of natural landscapes and direct access to walking and cycling trails. Perfect for nature lovers.",
                    "Zoned for hospitality use, this unique site is located in the historic district. Close to museums and tourist attractions. Ideal for a themed boutique hotel or high-end airbnb complex.",
                    "Wide-open pasture land with rich native grasses. Fully fenced with multiple water troughs. Suitable for cattle grazing, horse ranching, or large-scale solar farm installation."
                )

                val owners = listOf(
                    "John D.", "Sarah W.", "Michael K.", "Emily C.",
                    "David M.", "Jessica L.", "Andrew T.", "Olivia H.", "Brian O.",
                    "Alice N.", "Peter J.", "Mary S.", "James B.", "Kevin W.",
                    "Fiona M.", "George K.", "Grace P.", "Samuel L.", "Linda T.","Ram J"
                )

                val now = System.currentTimeMillis()
                val dayInMillis = 86400000L
                val listings = (1..200).map { i ->
                    val index = (i - 1) % titles.size
                    val title = titles[index]
                    val description = descriptions[index]
                    val owner = owners[index]
                    val price = 45000.0 + (i * 1250.0)
                    val phone = "+2547${(10000000..99999999).random()}"
                    """
                    {
                        "id": "$i",
                        "title": "$title #${100 + i}",
                        "description": "$description This property offers a unique investment opportunity with high potential for capital appreciation and versatile development options.",
                        "price": $price,
                        "imageUrl": "$BASE_IMAGE_URL=$i",
                        "isFavorite": false,
                        "createdAt": ${now - (i * 1000) - dayInMillis},
                        "phoneNumber": "$phone",
                        "ownerName": "$owner"
                    }
                    """.trimIndent()
                }
                "[${listings.joinToString(",")}]"
            }
            path.endsWith("listings") && method == METHOD_POST -> {
                // Echo back the body for create
                chain.request().body?.let { body ->
                    val buffer = okio.Buffer()
                    body.writeTo(buffer)
                    buffer.readUtf8()
                } ?: "{}"
            }
            path.contains("listings/") && method == METHOD_PUT -> {
                // update
                chain.request().body?.let { body ->
                    val buffer = okio.Buffer()
                    body.writeTo(buffer)
                    buffer.readUtf8()
                } ?: "{}"
            }
            path.endsWith("sync") && method == METHOD_POST -> {
                // sync
                chain.request().body?.let { body ->
                    val buffer = okio.Buffer()
                    body.writeTo(buffer)
                    buffer.readUtf8()
                } ?: "[]"
            }
            else -> "{}"
        }

        return Response.Builder()
            .code(API_SUCCESS_CODE)
            .message("OK")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .addHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .body(responseString.toByteArray().toResponseBody(CONTENT_TYPE_VALUE.toMediaType()))
            .build()
    }
}
