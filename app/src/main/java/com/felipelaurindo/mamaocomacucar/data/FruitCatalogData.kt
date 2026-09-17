package com.felipelaurindo.mamaocomacucar.data

/**
 * Represents a fruit catalog entry with botanical and cultural details.
 */
data class FruitCatalogItem(
    val name: String,
    val scientificName: String,
    val family: String,
    val biome: String,
    val category: String, // "Populares", "Cerrado", "Caatinga", "Amazônia", "Mata Atlântica", "Nativas Raras"
    val description: String,
    val id: String = ""
) {
    val canonicalId: String
        get() = id.ifEmpty { getFruitId(name) }
}

/**
 * Botanical catalog containing entries for all 71 species in ALLOWED_FRUITS.
 */
val FRUIT_CATALOG_LIST = listOf(
    FruitCatalogItem(
        id = "abacate",
        name = "Abacate",
        scientificName = "Persea americana",
        family = "Lauraceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Fruto de polpa cremosa e amanteigada, rico em gorduras saudáveis e nutrientes. Muito popular em quintais e pomares brasileiros."
    ),
    FruitCatalogItem(
        id = "abacaxi",
        name = "Abacaxi",
        scientificName = "Ananas comosus",
        family = "Bromeliaceae",
        biome = "Cerrado",
        category = "Populares",
        description = "Fruto tropical composto aromático e suculento, nativo da América do Sul e muito cultivado no Brasil."
    ),
    FruitCatalogItem(
        id = "acai",
        name = "Açaí",
        scientificName = "Euterpe oleracea",
        family = "Arecaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Palmeira amazônica cujas bagas arroxeadas são ricas em antioxidantes, energia e tradição culinária."
    ),
    FruitCatalogItem(
        id = "acerola",
        name = "Acerola",
        scientificName = "Malpighia emarginata",
        family = "Malpighiaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Famosa pelo altíssimo teor de vitamina C, possui frutos vermelhos brilhantes e polpa ácida refrescante."
    ),
    FruitCatalogItem(
        id = "ameixa_da_mata",
        name = "Ameixa-da-mata",
        scientificName = "Eugenia involucrata",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "Espécie nativa conhecida como cerejeira-do-rio-grande, com frutos arroxeados doces e polpa carnosa."
    ),
    FruitCatalogItem(
        id = "ameixa_amarela",
        name = "Ameixa-amarela / Nêspera",
        scientificName = "Eriobotrya japonica",
        family = "Rosaceae",
        biome = "Subtropical",
        category = "Populares",
        description = "Também conhecida como nêspera, possui frutos amarelo-dourados aveludados de polpa doce e refrescante, muito apreciada no inverno e na primavera."
    ),
    FruitCatalogItem(
        id = "amora",
        name = "Amora",
        scientificName = "Morus nigra",
        family = "Moraceae",
        biome = "Urbana",
        category = "Populares",
        description = "Árvore muito comum em calçadas e praças urbanas, com infrutescências suculentas e doces muito atrativas a pássaros."
    ),
    FruitCatalogItem(
        id = "araca",
        name = "Araçá",
        scientificName = "Psidium cattleyanum",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "Parente silvestre da goiaba, de frutos amarelos ou avermelhados com sabor ácido-adocicado irresistível."
    ),
    FruitCatalogItem(
        id = "araticum",
        name = "Araticum",
        scientificName = "Annona crassiflora",
        family = "Annonaceae",
        biome = "Cerrado",
        category = "Cerrado",
        description = "Grande fruto do cerrado de casca grossa e polpa amarela cremosa muito perfumada, consumido in natura ou em doces."
    ),
    FruitCatalogItem(
        id = "atemoia",
        name = "Atemoia",
        scientificName = "Annona squamosa x Annona cherimola",
        family = "Annonaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Híbrido entre a pinha e a cherimoia; frutos grandes e polpa branca extremamente doce com menos sementes."
    ),
    FruitCatalogItem(
        id = "bacuri",
        name = "Bacuri",
        scientificName = "Platonia insignis",
        family = "Clusiaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Fruto nobre do norte e nordeste com casca espessa e polpa aveludada branca de perfume inconfundível."
    ),
    FruitCatalogItem(
        id = "banana",
        name = "Banana",
        scientificName = "Musa acuminata",
        family = "Musaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Uma das frutas mais consumidas no país, produzida em cachos contínuos por bananeiras em quintais e matas."
    ),
    FruitCatalogItem(
        id = "biriba",
        name = "Biribá",
        scientificName = "Rollinia mucosa",
        family = "Annonaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Anonácea tropical de casca amarela com espinhos carnosos macios e polpa gelatinosa doce e translúcida."
    ),
    FruitCatalogItem(
        id = "buriti",
        name = "Buriti",
        scientificName = "Mauritia flexuosa",
        family = "Arecaceae",
        biome = "Cerrado",
        category = "Cerrado",
        description = "A 'árvore da vida' dos brejos e veredas, com frutos escamosos avermelhados riquíssimos em vitamina A e óleos nobres."
    ),
    FruitCatalogItem(
        id = "cacau",
        name = "Cacau",
        scientificName = "Theobroma cacao",
        family = "Malvaceae",
        biome = "Mata Atlântica",
        category = "Populares",
        description = "Fruto que dá origem ao chocolate; sua polpa branca doce e ácida envolve as sementes que são fermentadas e torradas."
    ),
    FruitCatalogItem(
        id = "cagaita",
        name = "Cagaita",
        scientificName = "Eugenia dysenterica",
        family = "Myrtaceae",
        biome = "Cerrado",
        category = "Cerrado",
        description = "Fruta clássica do cerrado brasileiro, de casca amarelo-pálida e sabor refrescante e suave."
    ),
    FruitCatalogItem(
        id = "cajaiba",
        name = "Cajaíba",
        scientificName = "Spondias dulcis",
        family = "Anacardiaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Conhecida também como cajá-manga, frutifica em cachos pesados com polpa crocante, ácida e aromática."
    ),
    FruitCatalogItem(
        id = "caja",
        name = "Cajá / Taperebá",
        scientificName = "Spondias mombin",
        family = "Anacardiaceae",
        biome = "Nativa",
        category = "Populares",
        description = "Conhecida como cajá no Nordeste e Sudeste e taperebá na Amazônia. Pequena drupa amarela de perfume marcante e sabor agridoce vibrante, muito usada em sucos e sorvetes."
    ),
    FruitCatalogItem(
        id = "caju",
        name = "Caju",
        scientificName = "Anacardium occidentale",
        family = "Anacardiaceae",
        biome = "Caatinga",
        category = "Populares",
        description = "O verdadeiro fruto botânico é a castanha, sustentada pelo pedúnculo suculento, carnoso e rico em vitamina C."
    ),
    FruitCatalogItem(
        id = "camu_camu",
        name = "Camu-camu",
        scientificName = "Myrciaria dubia",
        family = "Myrtaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Arbusto das margens de rios amazônicos com frutos avermelhados que possuem a maior concentração de vitamina C do reino vegetal."
    ),
    FruitCatalogItem(
        id = "cambuci",
        name = "Cambuci",
        scientificName = "Campomanesia phaea",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "Fruto em formato de disco voador típico da Serra do Mar, com acidez marcante e perfume intenso, ideal para licores e geleias."
    ),
    FruitCatalogItem(
        id = "carambola",
        name = "Carambola",
        scientificName = "Averrhoa carambola",
        family = "Oxalidaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Corte transversal em forma de estrela; polpa crocante, translúcida e refrescante, muito apreciada in natura e em sucos."
    ),
    FruitCatalogItem(
        id = "carnauba",
        name = "Carnaúba",
        scientificName = "Copernicia prunifera",
        family = "Arecaceae",
        biome = "Caatinga",
        category = "Caatinga",
        description = "Palmeira do semiárido cujos pequenos frutos pretos comestíveis são fonte de alimento para fauna e comunidades locais."
    ),
    FruitCatalogItem(
        id = "cherimoia",
        name = "Cherimoia",
        scientificName = "Annona cherimola",
        family = "Annonaceae",
        biome = "Nativa",
        category = "Nativas Raras",
        description = "Fruto da família das anonáceas com casca com marcas semelhantes a escamas ou impressões digitais e polpa aveludada."
    ),
    FruitCatalogItem(
        id = "ciriguela",
        name = "Ciriguela / Seriguela",
        scientificName = "Spondias purpurea",
        family = "Anacardiaceae",
        biome = "Cerrado",
        category = "Populares",
        description = "Também grafada como seriguela, produz frutinhos ovais amarelo-avermelhados doces e saborosos, que nascem diretamente nos ramos e encantam no verão."
    ),
    FruitCatalogItem(
        id = "coco",
        name = "Coco",
        scientificName = "Cocos nucifera",
        family = "Arecaceae",
        biome = "Litoral",
        category = "Populares",
        description = "Emblema dos litorais brasileiros, fornece água fresca isotônica natural e polpa nutritiva em todas as fases."
    ),
    FruitCatalogItem(
        id = "cupuacu",
        name = "Cupuaçu",
        scientificName = "Theobroma grandiflorum",
        family = "Malvaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Parente próximo do cacau, com grande casca lenhosa marrom e polpa branca cremosa de aroma intenso e sabor único."
    ),
    FruitCatalogItem(
        id = "figo",
        name = "Figo",
        scientificName = "Ficus carica",
        family = "Moraceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Cultivado desde a antiguidade e muito presente em quintais e chácaras no Brasil. Fruto doce e macio que na verdade é uma inflorescência invertida (sicônio)."
    ),
    FruitCatalogItem(
        id = "fruta_pao",
        name = "Fruta-pão",
        scientificName = "Artocarpus altilis",
        family = "Moraceae",
        biome = "Litoral",
        category = "Populares",
        description = "Fruto volumoso rico em amido, tradicionalmente assado ou cozido, muito cultivado no litoral e praças brasileiras."
    ),
    FruitCatalogItem(
        id = "fruta_do_conde",
        name = "Fruta-do-conde / Pinha",
        scientificName = "Annona squamosa",
        family = "Annonaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Também chamada de pinha ou ata, possui carpelos arredondados bem destacados e polpa branca açucarada extremamente perfumada e cremosa."
    ),
    FruitCatalogItem(
        id = "gabiroba",
        name = "Gabiroba",
        scientificName = "Campomanesia adamantium",
        family = "Myrtaceae",
        biome = "Cerrado",
        category = "Cerrado",
        description = "Frutinho do cerrado com casca verde-amarelada e sabor doce aromático inesquecível de infância no campo."
    ),
    FruitCatalogItem(
        id = "goiaba",
        name = "Goiaba",
        scientificName = "Psidium guajava",
        family = "Myrtaceae",
        biome = "Nativa",
        category = "Populares",
        description = "Onipresente nos quintais brasileiros, com polpa vermelha ou branca aromática, base da tradicional goiabada cascão."
    ),
    FruitCatalogItem(
        id = "graviola",
        name = "Graviola",
        scientificName = "Annona muricata",
        family = "Annonaceae",
        biome = "Amazônia",
        category = "Populares",
        description = "Grande fruto espinhoso verde com polpa branca fibrosa, cremosa e agridoce, muito apreciada em sucos e sorvetes."
    ),
    FruitCatalogItem(
        id = "grumixama",
        name = "Grumixama",
        scientificName = "Eugenia brasiliensis",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "A 'cereja brasileira', fruto preto-brilhante ou amarelo de polpa doce e suculenta da Mata Atlântica."
    ),
    FruitCatalogItem(
        id = "guarana",
        name = "Guaraná",
        scientificName = "Paullinia cupana",
        family = "Sapindaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Fruto amazônico com aspecto de 'olho', cujas sementes estimulantes são ricas em cafeína e base de bebidas energéticas."
    ),
    FruitCatalogItem(
        id = "jabuticaba",
        name = "Jabuticaba",
        scientificName = "Plinia cauliflora",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Populares",
        description = "Pérola negra que brota diretamente no tronco da árvore; fruto doce de polpa branca translúcida amado em todo o país."
    ),
    FruitCatalogItem(
        id = "jaca",
        name = "Jaca",
        scientificName = "Artocarpus heterophyllus",
        family = "Moraceae",
        biome = "Urbana",
        category = "Populares",
        description = "Um dos maiores frutos arbóreos do mundo; gomos doces e aromáticos divididos entre as variedades dura e mole."
    ),
    FruitCatalogItem(
        id = "jambo",
        name = "Jambo",
        scientificName = "Syzygium malaccense",
        family = "Myrtaceae",
        biome = "Urbana",
        category = "Populares",
        description = "Fruto piriforme de casca vermelha-púrpura aveludada e polpa branca esponjosa de suave perfume floral de rosas."
    ),
    FruitCatalogItem(
        id = "jambolao",
        name = "Jambolão",
        scientificName = "Syzygium cumini",
        family = "Myrtaceae",
        biome = "Urbana",
        category = "Populares",
        description = "Árvore urbana comum com bagas arroxeadas brilhantes que mancham a boca de roxo, ricas em antocianinas."
    ),
    FruitCatalogItem(
        id = "jandiroba",
        name = "Jandiroba",
        scientificName = "Fevillea cordifolia",
        family = "Cucurbitaceae",
        biome = "Mata Atlântica",
        category = "Nativas Raras",
        description = "Trepadeira silvestre com frutos capsulares globosos de casca dura que guardam sementes oleaginosas medicinais."
    ),
    FruitCatalogItem(
        id = "jaracatia",
        name = "Jaracatiá",
        scientificName = "Jacaratia spinosa",
        family = "Caricaceae",
        biome = "Mata Atlântica",
        category = "Nativas Raras",
        description = "Parente silvestre do mamão nativo da floresta atlântica; tanto os frutos amarelos quanto o tronco ralado viram doces tradicionais."
    ),
    FruitCatalogItem(
        id = "jatoba",
        name = "Jatobá",
        scientificName = "Hymenaea courbaril",
        family = "Fabaceae",
        biome = "Cerrado",
        category = "Cerrado",
        description = "Vagem lenhosa marrom muito dura contendo sementes envoltas em farinha amarelada adocicada de alto valor nutritivo."
    ),
    FruitCatalogItem(
        id = "jenipapo",
        name = "Jenipapo",
        scientificName = "Genipa americana",
        family = "Rubiaceae",
        biome = "Nativa",
        category = "Populares",
        description = "Fruto aromático de polpa escura doce e vinosa, usado no tradicional licor de jenipapo e na pintura corporal indígena."
    ),
    FruitCatalogItem(
        id = "jua",
        name = "Juá",
        scientificName = "Ziziphus joazeiro",
        family = "Rhamnaceae",
        biome = "Caatinga",
        category = "Caatinga",
        description = "Árvore símbolo da resistência do semiárido, com frutos amarelos doces ricos em saponinas e vitamina C."
    ),
    FruitCatalogItem(
        id = "laranja",
        name = "Laranja",
        scientificName = "Citrus sinensis",
        family = "Rutaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Cítrico mais cultivado e consumido do Brasil. Árvore perene com flores brancas aromáticas (flor-de-laranjeira) e frutos ricos em vitamina C e suco refrescante."
    ),
    FruitCatalogItem(
        id = "limao",
        name = "Limão",
        scientificName = "Citrus limon / Citrus latifolia",
        family = "Rutaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Presente na grande maioria dos quintais brasileiros, em especial o Limão-taiti e o Limão-cravo. Indispensável para temperos, sucos e remédios caseiros."
    ),
    FruitCatalogItem(
        id = "maba",
        name = "Maba",
        scientificName = "Diospyros inconstans",
        family = "Ebenaceae",
        biome = "Nativa",
        category = "Nativas Raras",
        description = "Caqui-do-mato brasileiro, fruto silvestre arredondado alaranjado de polpa adocicada quando bem maduro."
    ),
    FruitCatalogItem(
        id = "maca",
        name = "Maçã",
        scientificName = "Malus domestica",
        family = "Rosaceae",
        biome = "Subtropical",
        category = "Populares",
        description = "Uma das frutas mais populares do mundo. Árvore adaptada a climas amenos, com frutos crocantes, suculentos e de grande valor nutricional."
    ),
    FruitCatalogItem(
        id = "macauba",
        name = "Macaúba",
        scientificName = "Acrocomia aculeata",
        family = "Arecaceae",
        biome = "Cerrado",
        category = "Cerrado",
        description = "Palmeira rústica e espinhosa com frutos globosos oleaginosos de polpa amarela nutritiva e castanha crocante."
    ),
    FruitCatalogItem(
        id = "mamao",
        name = "Mamão",
        scientificName = "Carica papaya",
        family = "Caricaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "O fruto que dá nome ao app! Casca alaranjada com polpa doce e sementes pretas centrais, símbolo da tropicalidade brasileira."
    ),
    FruitCatalogItem(
        id = "manga",
        name = "Manga",
        scientificName = "Mangifera indica",
        family = "Anacardiaceae",
        biome = "Urbana",
        category = "Populares",
        description = "Reina nas praças e quintais urbanos do Brasil, com copas frondosas e frutos suculentos e aromáticos."
    ),
    FruitCatalogItem(
        id = "mangaba",
        name = "Mangaba",
        scientificName = "Hancornia speciosa",
        family = "Apocynaceae",
        biome = "Cerrado",
        category = "Cerrado",
        description = "Seu nome em tupi significa 'coisa boa de comer'; polpa macia, perfumada e leitosa muito valorizada no nordeste."
    ),
    FruitCatalogItem(
        id = "maracuja",
        name = "Maracujá",
        scientificName = "Passiflora edulis",
        family = "Passifloraceae",
        biome = "Nativa",
        category = "Populares",
        description = "Fruto de trepadeira com flor deslumbrante e polpa amarela aromática conhecida por suas propriedades calmantes."
    ),
    FruitCatalogItem(
        id = "melancia",
        name = "Melancia",
        scientificName = "Citrullus lanatus",
        family = "Cucurbitaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Grande fruto rasteiro composto por mais de 90% de água, incrivelmente refrescante e doce nas tardes quentes."
    ),
    FruitCatalogItem(
        id = "melao",
        name = "Melão",
        scientificName = "Cucumis melo",
        family = "Cucurbitaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Aromático e suculento com polpa clara suave, largamente cultivado no vale do São Francisco e consumido em todo o país."
    ),
    FruitCatalogItem(
        id = "mexerica",
        name = "Mexerica",
        scientificName = "Citrus reticulata",
        family = "Rutaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Também chamada de tangerina ou bergamota, perfuma o ambiente ao ser descascada com as mãos e possui gomos doces e ácidos."
    ),
    FruitCatalogItem(
        id = "morango",
        name = "Morango",
        scientificName = "Fragaria x ananassa",
        family = "Rosaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Pseudofruto vermelho brilhante pontilhado por aquênios, união perfeita de doçura e leve acidez."
    ),
    FruitCatalogItem(
        id = "murici",
        name = "Murici",
        scientificName = "Byrsonima verbascifolia",
        family = "Malpighiaceae",
        biome = "Cerrado",
        category = "Cerrado",
        description = "Frutinho amarelo do cerrado de aroma forte e marcante e polpa carnosa amanteigada com sabor característico."
    ),
    FruitCatalogItem(
        id = "pequi",
        name = "Pequi",
        scientificName = "Caryocar brasiliense",
        family = "Caryocaraceae",
        biome = "Cerrado",
        category = "Cerrado",
        description = "O rei do cerrado! Fruto aromático de polpa amarelo-ouro que deve ser roída com cuidado devido aos espinhos internos."
    ),
    FruitCatalogItem(
        id = "pessego",
        name = "Pêssego",
        scientificName = "Prunus persica",
        family = "Rosaceae",
        biome = "Subtropical",
        category = "Populares",
        description = "Árvore de linda floração primaveril rosada e frutos aveludados de polpa suculenta, doce e aromática, cultivada especialmente nas regiões Sul e Sudeste."
    ),
    FruitCatalogItem(
        id = "pitaia",
        name = "Pitaia",
        scientificName = "Selenicereus undatus",
        family = "Cactaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Fruto exótico de cacto com casca escamosa rosa-choque e polpa branca salpicada de sementinhas pretas crocantes."
    ),
    FruitCatalogItem(
        id = "pitanga",
        name = "Pitanga",
        scientificName = "Eugenia uniflora",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Populares",
        description = "Fruta nativa brasileira com formato canelado inconfundível; varia do coral ao vermelho-rubi e perfuma os quintais."
    ),
    FruitCatalogItem(
        id = "pupunha",
        name = "Pupunha",
        scientificName = "Bactris gasipaes",
        family = "Arecaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Frutos de palmeira consumidos tradicionalmente cozidos com café no café da manhã amazônico, ricos em energia e carotenoides."
    ),
    FruitCatalogItem(
        id = "roma",
        name = "Romã",
        scientificName = "Punica granatum",
        family = "Lythraceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Muito tradicional em calçadas e jardins residenciais brasileiros. Seus frutos avermelhados com coroa possuem sementes translúcidas ricas em antioxidantes."
    ),
    FruitCatalogItem(
        id = "sapota",
        name = "Sapota",
        scientificName = "Quararibea cordata",
        family = "Malpighiaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Sapota-do-solimões, grande fruto arredondado com casca parda e polpa alaranjada doce, fibrosa e muito saborosa."
    ),
    FruitCatalogItem(
        id = "sapoti",
        name = "Sapoti",
        scientificName = "Manilkara zapota",
        family = "Sapotaceae",
        biome = "Litoral",
        category = "Populares",
        description = "Fruto oval de casca marrom áspera e polpa incrivelmente doce que lembra mel e açúcar mascavo."
    ),
    FruitCatalogItem(
        id = "tamarindo",
        name = "Tamarindo",
        scientificName = "Tamarindus indica",
        family = "Fabaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Vagem marrom rígida com polpa densa agridoce muito utilizada em sucos refrescantes, molhos e compotas."
    ),
    FruitCatalogItem(
        id = "tucuma",
        name = "Tucumã",
        scientificName = "Astrocaryum aculeatum",
        family = "Arecaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Fruto de palmeira amazônica com casca alaranjada e polpa amarela rica em ômega-3, famosa no sanduíche X-Caboquinho."
    ),
    FruitCatalogItem(
        id = "umbu",
        name = "Umbu",
        scientificName = "Spondias tuberosa",
        family = "Anacardiaceae",
        biome = "Caatinga",
        category = "Caatinga",
        description = "O fruto sagrado do sertão; árvore com batatas subterrâneas que armazenam água, produzindo drupas verdes sumarentas e agridoces."
    ),
    FruitCatalogItem(
        id = "uva",
        name = "Uva",
        scientificName = "Vitis vinifera",
        family = "Vitaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Cultivada em parreirais de norte a sul do país, tanto para consumo fresco de mesa quanto para vinhos e sucos artesanais."
    ),
    FruitCatalogItem(
        id = "uvaia",
        name = "Uvaia",
        scientificName = "Eugenia pyriformis",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "Fruto amarelo aveludado muito aromático da Mata Atlântica, com polpa suculenta e sabor deliciosamente acidulado."
    )
)
