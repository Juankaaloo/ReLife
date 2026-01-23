package com.relife.data.repository

import com.relife.data.model.*

object MockData {
    
    val currentUser = User(
        id = "user_1",
        name = "María García",
        username = "mariagarcia",
        email = "maria@example.com",
        avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
        coverUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800",
        bio = "Creadora de objetos únicos ♻️ | Amante del upcycling | Madrid 🇪🇸",
        website = "www.mariacreaciones.com",
        isVerified = true,
        isOnline = true,
        followersCount = 1234,
        followingCount = 567,
        postsCount = 48,
        totalLikes = 5420
    )
    
    val users = listOf(
        UserPreview(
            id = "user_2",
            name = "Carlos Ruiz",
            username = "carlosruiz",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            isVerified = true,
            followersCount = 2341
        ),
        UserPreview(
            id = "user_3",
            name = "Ana Martínez",
            username = "anamartinez",
            avatarUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
            isVerified = false,
            isOnline = true,
            followersCount = 892
        ),
        UserPreview(
            id = "user_4",
            name = "Pablo Sánchez",
            username = "pablosanchez",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
            isVerified = true,
            followersCount = 3456
        ),
        UserPreview(
            id = "user_5",
            name = "Laura López",
            username = "lauralopez",
            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150",
            isVerified = false,
            followersCount = 1567
        ),
        UserPreview(
            id = "user_6",
            name = "Diego Fernández",
            username = "diegofernandez",
            avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150",
            isVerified = true,
            isOnline = true,
            followersCount = 4123
        )
    )
    
    val posts = listOf(
        Post(
            id = "post_1",
            title = "Mesa de palet restaurada",
            description = "Convertí un viejo palet en una mesa de centro moderna. Solo necesité lijadora, barniz y unas patas de hairpin.",
            imageUrl = "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600",
            author = users[0],
            likesCount = 245,
            commentsCount = 32,
            isLiked = true,
            tags = listOf("palet", "mesa", "DIY", "restauración"),
            category = PostCategory.FURNITURE
        ),
        Post(
            id = "post_2",
            title = "Lámpara con botellas de vidrio",
            description = "Reutilicé botellas de vino para crear esta lámpara única. Tutorial completo en mi perfil.",
            imageUrl = "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=600",
            author = users[1],
            likesCount = 189,
            commentsCount = 24,
            tags = listOf("lámpara", "botellas", "iluminación", "reciclaje"),
            category = PostCategory.LIGHTING
        ),
        Post(
            id = "post_3",
            title = "Maceteros con neumáticos",
            description = "Dale vida a tu jardín con estos coloridos maceteros hechos con neumáticos viejos.",
            imageUrl = "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=600",
            author = users[2],
            likesCount = 312,
            commentsCount = 45,
            isSaved = true,
            tags = listOf("jardín", "neumáticos", "maceteros", "plantas"),
            category = PostCategory.GARDEN
        ),
        Post(
            id = "post_4",
            title = "Bolso con vaqueros viejos",
            description = "Transformé unos jeans rotos en este bolso único. ¡Zero waste fashion!",
            imageUrl = "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600",
            author = users[3],
            likesCount = 423,
            commentsCount = 56,
            tags = listOf("moda", "vaqueros", "bolso", "zerowaste"),
            category = PostCategory.FASHION
        ),
        Post(
            id = "post_5",
            title = "Estantería con cajas de fruta",
            description = "Cajas de fruta + pintura = estantería perfecta para el salón.",
            imageUrl = "https://images.unsplash.com/photo-1594620302200-9a762244a156?w=600",
            author = users[4],
            likesCount = 287,
            commentsCount = 38,
            tags = listOf("estantería", "cajas", "decoración", "DIY"),
            category = PostCategory.DECORATION
        ),
        Post(
            id = "post_6",
            title = "Cargador solar con componentes reciclados",
            description = "Construí este cargador solar usando células de paneles viejos.",
            imageUrl = "https://images.unsplash.com/photo-1509391366360-2e959784a276?w=600",
            author = users[0],
            likesCount = 567,
            commentsCount = 89,
            tags = listOf("tech", "solar", "cargador", "electrónica"),
            category = PostCategory.TECH
        )
    )
    
    val products = listOf(
        Product(
            id = "prod_1",
            title = "Mesa de centro industrial",
            description = "Mesa hecha con palets y tubería de cobre. Perfecta para salones modernos.",
            price = 149.99,
            images = listOf(
                "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600",
                "https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=600"
            ),
            seller = users[0],
            category = PostCategory.FURNITURE,
            condition = ProductCondition.RESTORED,
            shippingAvailable = true,
            location = "Madrid",
            rating = 4.8f,
            reviewsCount = 12
        ),
        Product(
            id = "prod_2",
            title = "Lámpara colgante vintage",
            description = "Lámpara única hecha con botellas de vidrio y madera recuperada.",
            price = 79.50,
            images = listOf(
                "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=600"
            ),
            seller = users[1],
            category = PostCategory.LIGHTING,
            condition = ProductCondition.NEW,
            shippingAvailable = true,
            location = "Barcelona",
            rating = 4.9f,
            reviewsCount = 8
        ),
        Product(
            id = "prod_3",
            title = "Set de maceteros coloridos",
            description = "3 maceteros de diferentes tamaños hechos con neumáticos reciclados.",
            price = 45.00,
            images = listOf(
                "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=600"
            ),
            seller = users[2],
            category = PostCategory.GARDEN,
            condition = ProductCondition.NEW,
            shippingAvailable = false,
            location = "Valencia",
            rating = 4.5f,
            reviewsCount = 5
        ),
        Product(
            id = "prod_4",
            title = "Bolso tote denim",
            description = "Bolso grande y resistente hecho 100% con vaqueros reciclados.",
            price = 35.00,
            images = listOf(
                "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600"
            ),
            seller = users[3],
            category = PostCategory.FASHION,
            condition = ProductCondition.NEW,
            shippingAvailable = true,
            location = "Sevilla",
            rating = 4.7f,
            reviewsCount = 15,
            isFavorite = true
        ),
        Product(
            id = "prod_5",
            title = "Estantería rústica",
            description = "Estantería de 4 niveles hecha con cajas de fruta tratadas.",
            price = 89.00,
            images = listOf(
                "https://images.unsplash.com/photo-1594620302200-9a762244a156?w=600"
            ),
            seller = users[4],
            category = PostCategory.DECORATION,
            condition = ProductCondition.RESTORED,
            shippingAvailable = true,
            location = "Bilbao",
            rating = 4.6f,
            reviewsCount = 7
        )
    )
    
    val notifications = listOf(
        Notification(
            id = "notif_1",
            type = NotificationType.LIKE,
            fromUser = users[0],
            message = "le gustó tu publicación",
            relatedImageUrl = "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=100",
            relatedPostId = "post_1",
            createdAt = System.currentTimeMillis() - 3600000
        ),
        Notification(
            id = "notif_2",
            type = NotificationType.COMMENT,
            fromUser = users[1],
            message = "comentó: ¡Increíble trabajo!",
            relatedImageUrl = "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=100",
            relatedPostId = "post_2",
            createdAt = System.currentTimeMillis() - 7200000
        ),
        Notification(
            id = "notif_3",
            type = NotificationType.FOLLOW,
            fromUser = users[2],
            message = "comenzó a seguirte",
            createdAt = System.currentTimeMillis() - 10800000
        ),
        Notification(
            id = "notif_4",
            type = NotificationType.SALE,
            fromUser = users[3],
            message = "compró Mesa de centro industrial",
            relatedProductId = "prod_1",
            createdAt = System.currentTimeMillis() - 86400000
        ),
        Notification(
            id = "notif_5",
            type = NotificationType.REVIEW,
            fromUser = users[4],
            message = "dejó una reseña de 5 estrellas",
            relatedProductId = "prod_1",
            createdAt = System.currentTimeMillis() - 172800000,
            isRead = true
        ),
        Notification(
            id = "notif_6",
            type = NotificationType.MENTION,
            fromUser = users[0],
            message = "te mencionó en un comentario",
            relatedPostId = "post_3",
            createdAt = System.currentTimeMillis() - 259200000,
            isRead = true
        )
    )
    
    val conversations = listOf(
        Conversation(
            id = "conv_1",
            participant = users[0],
            lastMessage = "¡Hola! Me interesa la mesa, ¿sigue disponible?",
            lastMessageTime = System.currentTimeMillis() - 1800000,
            unreadCount = 2,
            isOnline = true
        ),
        Conversation(
            id = "conv_2",
            participant = users[1],
            lastMessage = "Gracias por la compra!",
            lastMessageTime = System.currentTimeMillis() - 86400000,
            unreadCount = 0,
            isOnline = false
        ),
        Conversation(
            id = "conv_3",
            participant = users[2],
            lastMessage = "¿Podrías hacer envío a Valencia?",
            lastMessageTime = System.currentTimeMillis() - 172800000,
            unreadCount = 1,
            isOnline = true
        ),
        Conversation(
            id = "conv_4",
            participant = users[3],
            lastMessage = "Me encanta tu trabajo!",
            lastMessageTime = System.currentTimeMillis() - 259200000,
            unreadCount = 0,
            isOnline = false
        )
    )
    
    val userStats = UserStats(
        views = 12450,
        viewsChange = 12.5f,
        followers = 1234,
        followersChange = 8.3f,
        totalLikes = 5420,
        likesChange = 15.2f,
        comments = 892,
        commentsChange = -2.1f,
        salesStats = SalesStats(
            totalIncome = 2340.50,
            monthlyIncome = 450.00,
            productsOnSale = 8,
            pendingOrders = 3
        ),
        weeklyActivity = listOf(
            DayActivity("Lun", 45),
            DayActivity("Mar", 62),
            DayActivity("Mié", 38),
            DayActivity("Jue", 71),
            DayActivity("Vie", 55),
            DayActivity("Sáb", 89),
            DayActivity("Dom", 67)
        ),
        engagement = EngagementStats(
            likesRate = 0.78f,
            commentsRate = 0.45f,
            sharesRate = 0.23f,
            savesRate = 0.56f
        ),
        recentTransactions = listOf(
            Transaction(
                id = "trans_1",
                productTitle = "Mesa de centro industrial",
                amount = 149.99,
                type = TransactionType.SALE,
                date = System.currentTimeMillis() - 86400000
            ),
            Transaction(
                id = "trans_2",
                productTitle = "Lámpara colgante vintage",
                amount = 79.50,
                type = TransactionType.SALE,
                date = System.currentTimeMillis() - 259200000
            )
        ),
        topProducts = listOf(
            ProductPreview(
                id = "prod_1",
                title = "Mesa de centro industrial",
                imageUrl = "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=100",
                views = 1234
            ),
            ProductPreview(
                id = "prod_2",
                title = "Lámpara colgante vintage",
                imageUrl = "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=100",
                views = 987
            )
        ),
        audienceInsights = AudienceInsights(
            topAgeRange = "25-34",
            topCity = "Madrid",
            bestHour = "19:00",
            bestDay = "Sábado"
        )
    )
    
    val trendingTags = listOf(
        "#Upcycling" to 2345,
        "#PaletDIY" to 1890,
        "#ZeroWaste" to 1567,
        "#VintageDecor" to 1234,
        "#Reciclaje" to 1123,
        "#Handmade" to 998,
        "#Sostenible" to 876,
        "#DIY" to 765
    )
    
    val stories = users.map { user ->
        Story(
            id = "story_${user.id}",
            user = user,
            imageUrl = "https://images.unsplash.com/photo-${(1000000000..9999999999).random()}?w=400",
            isViewed = (0..1).random() == 1
        )
    }
}

data class Story(
    val id: String,
    val user: UserPreview,
    val imageUrl: String,
    val isViewed: Boolean = false
)
