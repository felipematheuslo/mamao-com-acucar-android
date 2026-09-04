package com.felipelaurindo.mamaocomacucar.data

/**
 * Represents a fruit catalog entry with botanical and cultural details.
 */
data class FruitCatalogItem(
    val name: String,
    val scientificName: String,
    val family: String,
    val biome: String,
    val category: String, // "Populares", "Cerrado & Caatinga", "Amazônia", "Mata Atlântica", "Nativas Raras"
    val description: String
)

/**
 * Botanical catalog containing entries for all 68 species in ALLOWED_FRUITS.
 */
val FRUIT_CATALOG_LIST = listOf(
    FruitCatalogItem(
        name = "Abacaxi",
        scientificName = "Ananas comosus",
        family = "Bromeliaceae",
        biome = "Cerrado",
        category = "Populares",
        description = "Fruto tropical composto aromático e suculento, nativo da América do Sul e muito cultivado no Brasil."
    ),
    FruitCatalogItem(
        name = "Açaí",
        scientificName = "Euterpe oleracea",
        family = "Arecaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Palmeira amazônica cujas bagas arroxeadas são ricas em antioxidantes, energia e tradição culinária."
    ),
    FruitCatalogItem(
        name = "Acerola",
        scientificName = "Malpighia emarginata",
        family = "Malpighiaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Famosa pelo altíssimo teor de vitamina C, possui frutos vermelhos brilhantes e polpa ácida refrescante."
    ),
    FruitCatalogItem(
        name = "Ameixa-da-mata",
        scientificName = "Eugenia involucrata",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "Espécie nativa conhecida como cerejeira-do-rio-grande, com frutos arroxeados doces e polpa carnosa."
    ),
    FruitCatalogItem(
        name = "Ameixa-amarela",
        scientificName = "Eriobotrya japonica",
        family = "Rosaceae",
        biome = "Subtropical",
        category = "Populares",
        description = "Também conhecida como nêspera, possui frutos amarelo-dourados aveludados, muito apreciados no inverno."
    ),
    FruitCatalogItem(
        name = "Amora",
        scientificName = "Morus nigra",
        family = "Moraceae",
        biome = "Urbana",
        category = "Populares",
        description = "Árvore muito comum em calçadas e praças urbanas, com infrutescências suculentas e doces muito atrativas a pássaros."
    ),
    FruitCatalogItem(
        name = "Araçá",
        scientificName = "Psidium cattleyanum",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "Parente silvestre da goiaba, de frutos menores, casca amarelada ou avermelhada e sabor perfumado agridoce."
    ),
    FruitCatalogItem(
        name = "Araticum",
        scientificName = "Annona crassiflora",
        family = "Annonaceae",
        biome = "Cerrado",
        category = "Cerrado & Caatinga",
        description = "Conhecido como marolo ou araticum-do-cerrado, fruto rústico de casca grossa e polpa amarela intensamente aromática."
    ),
    FruitCatalogItem(
        name = "Atemoia",
        scientificName = "Annona squamosa × A. cherimola",
        family = "Annonaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Híbrido entre a fruta-do-conde e a cherimoia, possui polpa branca extremamente doce, cremosa e poucas sementes."
    ),
    FruitCatalogItem(
        name = "Bacuri",
        scientificName = "Platonia insignis",
        family = "Clusiaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Fruto nobre do norte e nordeste com casca espessa e polpa aveludada branca de perfume inconfundível."
    ),
    FruitCatalogItem(
        name = "Banana",
        scientificName = "Musa acuminata",
        family = "Musaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Uma das frutas mais consumidas no país, produzida em cachos contínuos por bananeiras em quintais e matas."
    ),
    FruitCatalogItem(
        name = "Biribá",
        scientificName = "Rollinia mucosa",
        family = "Annonaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Anonácea tropical de casca amarela com espinhos carnosos macios e polpa gelatinosa doce e translúcida."
    ),
    FruitCatalogItem(
        name = "Buriti",
        scientificName = "Mauritia flexuosa",
        family = "Arecaceae",
        biome = "Cerrado",
        category = "Cerrado & Caatinga",
        description = "A 'árvore da vida' dos brejos e veredas, com frutos escamosos avermelhados riquíssimos em vitamina A e óleos nobres."
    ),
    FruitCatalogItem(
        name = "Cacau",
        scientificName = "Theobroma cacao",
        family = "Malvaceae",
        biome = "Mata Atlântica",
        category = "Populares",
        description = "Fruto que dá origem ao chocolate; sua polpa branca doce e ácida envolve as sementes que são fermentadas e torradas."
    ),
    FruitCatalogItem(
        name = "Cagaita",
        scientificName = "Eugenia dysenterica",
        family = "Myrtaceae",
        biome = "Cerrado",
        category = "Cerrado & Caatinga",
        description = "Fruta clássica do cerrado brasileiro, de casca amarelo-pálida e sabor refrescante e suave."
    ),
    FruitCatalogItem(
        name = "Cajaíba",
        scientificName = "Spondias dulcis",
        family = "Anacardiaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Conhecida também como cajá-manga, frutifica em cachos pesados com polpa crocante, ácida e aromática."
    ),
    FruitCatalogItem(
        name = "Cajá",
        scientificName = "Spondias mombin",
        family = "Anacardiaceae",
        biome = "Nativa",
        category = "Populares",
        description = "Pequena drupa amarela de perfume marcante e sabor agridoce vibrante, muito usada em sucos, picolés e polpas."
    ),
    FruitCatalogItem(
        name = "Caju",
        scientificName = "Anacardium occidentale",
        family = "Anacardiaceae",
        biome = "Caatinga",
        category = "Populares",
        description = "O verdadeiro fruto botânico é a castanha, sustentada pelo pedúnculo suculento, carnoso e rico em vitamina C."
    ),
    FruitCatalogItem(
        name = "Camu-camu",
        scientificName = "Myrciaria dubia",
        family = "Myrtaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Arbusto das margens de rios amazônicos com frutos avermelhados que possuem a maior concentração de vitamina C do reino vegetal."
    ),
    FruitCatalogItem(
        name = "Cambuci",
        scientificName = "Campomanesia phaea",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "Fruto verde em formato curioso de disco voador ou pião, símbolo histórico da Serra do Mar e da Mata Atlântica paulista."
    ),
    FruitCatalogItem(
        name = "Carambola",
        scientificName = "Averrhoa carambola",
        family = "Oxalidaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Fruta de 5 arestas longitudinais que, ao ser fatiada transversalmente, forma perfeitas estrelas amarelas translúcidas."
    ),
    FruitCatalogItem(
        name = "Carnaúba",
        scientificName = "Copernicia prunifera",
        family = "Arecaceae",
        biome = "Caatinga",
        category = "Cerrado & Caatinga",
        description = "A 'árvore da providência' do semiárido nordestino, cujos pequenos frutos escuros alimentam a fauna e populações locais."
    ),
    FruitCatalogItem(
        name = "Cherimoia",
        scientificName = "Annona cherimola",
        family = "Annonaceae",
        biome = "Serrana",
        category = "Nativas Raras",
        description = "Fruto da família das anonáceas com casca com marcas semelhantes a escamas ou impressões digitais e polpa aveludada."
    ),
    FruitCatalogItem(
        name = "Ciriguela",
        scientificName = "Spondias purpurea",
        family = "Anacardiaceae",
        biome = "Cerrado",
        category = "Populares",
        description = "Frutinhos ovais amarelo-avermelhados doces e saborosos, que nascem diretamente nos ramos e encantam no verão."
    ),
    FruitCatalogItem(
        name = "Coco",
        scientificName = "Cocos nucifera",
        family = "Arecaceae",
        biome = "Litoral",
        category = "Populares",
        description = "Emblema dos litorais brasileiros, fornece água fresca isotônica natural e polpa nutritiva em todas as fases."
    ),
    FruitCatalogItem(
        name = "Cupuaçu",
        scientificName = "Theobroma grandiflorum",
        family = "Malvaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Parente próximo do cacau, com grande casca lenhosa marrom e polpa branca cremosa de aroma intenso e sabor único."
    ),
    FruitCatalogItem(
        name = "Fruta-pão",
        scientificName = "Artocarpus altilis",
        family = "Moraceae",
        biome = "Litoral",
        category = "Populares",
        description = "Fruto volumoso rico em amido, tradicionalmente assado ou cozido, muito cultivado no litoral e praças brasileiras."
    ),
    FruitCatalogItem(
        name = "Fruta-do-conde",
        scientificName = "Annona squamosa",
        family = "Annonaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Também chamada de ata ou pinha, possui carpelos arredondados bem destacados e polpa branca açucarada."
    ),
    FruitCatalogItem(
        name = "Gabiroba",
        scientificName = "Campomanesia adamantium",
        family = "Myrtaceae",
        biome = "Cerrado",
        category = "Cerrado & Caatinga",
        description = "Frutinho do cerrado com casca verde-amarelada e sabor doce aromático inesquecível de infância no campo."
    ),
    FruitCatalogItem(
        name = "Goiaba",
        scientificName = "Psidium guajava",
        family = "Myrtaceae",
        biome = "Urbana",
        category = "Populares",
        description = "Uma das frutas mais populares em quintais urbanos brasileiros, com polpa vermelha ou branca doce e aromática."
    ),
    FruitCatalogItem(
        name = "Graviola",
        scientificName = "Annona muricata",
        family = "Annonaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Grande fruto verde espiculado com polpa branca levemente ácida, muito apreciada em sucos e sobremesas."
    ),
    FruitCatalogItem(
        name = "Grumixama",
        scientificName = "Eugenia brasiliensis",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "A 'cereja brasileira', fruto roxo-escuro brilhante com cálice persistente e sabor que une cereja e jabuticaba."
    ),
    FruitCatalogItem(
        name = "Guaraná",
        scientificName = "Paullinia cupana",
        family = "Sapindaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Planta sagrada da Amazônia, com frutos vermelhos abertos que lembram olhos humanos e sementes ricas em cafeína."
    ),
    FruitCatalogItem(
        name = "Jabuticaba",
        scientificName = "Plinia cauliflora",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Populares",
        description = "Espécie cauliflora nativa em que as flores e frutos roxo-escuros brotam diretamente no tronco e galhos."
    ),
    FruitCatalogItem(
        name = "Jaca",
        scientificName = "Artocarpus heterophyllus",
        family = "Moraceae",
        biome = "Urbana",
        category = "Populares",
        description = "Maior fruto de árvore do mundo, comum em encostas e parques, com gomos doces consumidos in natura ou cozidos."
    ),
    FruitCatalogItem(
        name = "Jambo",
        scientificName = "Syzygium malaccense",
        family = "Myrtaceae",
        biome = "Urbana",
        category = "Populares",
        description = "Árvore de copa densa e floração rosa exuberante, com frutos vermelhos brilhantes em formato de pêra e aroma floral."
    ),
    FruitCatalogItem(
        name = "Jambolão",
        scientificName = "Syzygium cumini",
        family = "Myrtaceae",
        biome = "Urbana",
        category = "Populares",
        description = "Drupas roxo-escuras abundantes em calçadas brasileiras, famosas por mancharem suavemente a língua de violeta."
    ),
    FruitCatalogItem(
        name = "Jandiroba",
        scientificName = "Fevillea cordifolia",
        family = "Cucurbitaceae",
        biome = "Amazônia",
        category = "Nativas Raras",
        description = "Fruto globoso rústico cujas sementes fornecem óleo medicinal e tradicional amplamente utilizado por ribeirinhos."
    ),
    FruitCatalogItem(
        name = "Jaracatiá",
        scientificName = "Jacaratia spinosa",
        family = "Caricaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "Parente silvestre do mamão nativo da floresta atlântica, com pequenos frutos amarelos de sabor picante-adocicado."
    ),
    FruitCatalogItem(
        name = "Jatobá",
        scientificName = "Hymenaea courbaril",
        family = "Fabaceae",
        biome = "Cerrado",
        category = "Cerrado & Caatinga",
        description = "Árvore majestosa com vagens lenhosas duras que guardam uma farinha amarelada rica em nutrientes e energia."
    ),
    FruitCatalogItem(
        name = "Jenipapo",
        scientificName = "Genipa americana",
        family = "Rubiaceae",
        biome = "Nativa",
        category = "Nativas Raras",
        description = "Usado tanto em licores e doces quanto para extração de pigmento azul natural por povos indígenas brasileiros."
    ),
    FruitCatalogItem(
        name = "Juá",
        scientificName = "Ziziphus joazeiro",
        family = "Rhamnaceae",
        biome = "Caatinga",
        category = "Cerrado & Caatinga",
        description = "Árvore símbolo da resistência do semiárido, com frutos amarelos doces ricos em saponinas e vitamina C."
    ),
    FruitCatalogItem(
        name = "Maba",
        scientificName = "Diospyros inconstans",
        family = "Ebenaceae",
        biome = "Nativa",
        category = "Nativas Raras",
        description = "Caqui-do-mato brasileiro, fruto silvestre arredondado alaranjado de polpa adocicada quando bem maduro."
    ),
    FruitCatalogItem(
        name = "Macaúba",
        scientificName = "Acrocomia aculeata",
        family = "Arecaceae",
        biome = "Cerrado",
        category = "Cerrado & Caatinga",
        description = "Palmeira rústica e espinhosa com frutos globosos oleaginosos de polpa amarela nutritiva e castanha crocante."
    ),
    FruitCatalogItem(
        name = "Mamão",
        scientificName = "Carica papaya",
        family = "Caricaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "O fruto que dá nome ao app! Casca alaranjada com polpa doce e sementes pretas centrais, símbolo da tropicalidade brasileira."
    ),
    FruitCatalogItem(
        name = "Manga",
        scientificName = "Mangifera indica",
        family = "Anacardiaceae",
        biome = "Urbana",
        category = "Populares",
        description = "Reina nas praças e quintais urbanos do Brasil, com copas frondosas e frutos suculentos e aromáticos."
    ),
    FruitCatalogItem(
        name = "Mangaba",
        scientificName = "Hancornia speciosa",
        family = "Apocynaceae",
        biome = "Cerrado",
        category = "Cerrado & Caatinga",
        description = "Seu nome em tupi significa 'coisa boa de comer'; polpa macia, perfumada e leitosa muito valorizada no nordeste."
    ),
    FruitCatalogItem(
        name = "Maracujá",
        scientificName = "Passiflora edulis",
        family = "Passifloraceae",
        biome = "Nativa",
        category = "Populares",
        description = "Fruto de trepadeira com flor deslumbrante e polpa amarela aromática conhecida por suas propriedades calmantes."
    ),
    FruitCatalogItem(
        name = "Melancia",
        scientificName = "Citrullus lanatus",
        family = "Cucurbitaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Fruto rasteiro volumoso com mais de 90% de água, polpa vermelha doce e crocante, perfeita para o calor do verão."
    ),
    FruitCatalogItem(
        name = "Melão",
        scientificName = "Cucumis melo",
        family = "Cucurbitaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Fruto arredondado de casca amarela ou rendilhada e polpa perfumada refrescante, cultivado no semiárido e hortas."
    ),
    FruitCatalogItem(
        name = "Mexerica",
        scientificName = "Citrus reticulata",
        family = "Rutaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Também chamada de tangerina ou bergamota; casca fácil de soltar com as mãos e gomos suculentos e perfumados."
    ),
    FruitCatalogItem(
        name = "Morango",
        scientificName = "Fragaria × ananassa",
        family = "Rosaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Pequeno fruto cônico vermelho com sementinhas na casca e sabor agridoce irresistível."
    ),
    FruitCatalogItem(
        name = "Murici",
        scientificName = "Byrsonima verbascifolia",
        family = "Malpighiaceae",
        biome = "Cerrado",
        category = "Cerrado & Caatinga",
        description = "Frutinho amarelo do cerrado de aroma forte e marcante e polpa carnosa amanteigada com sabor característico."
    ),
    FruitCatalogItem(
        name = "Nêspera",
        scientificName = "Eriobotrya japonica",
        family = "Rosaceae",
        biome = "Subtropical",
        category = "Populares",
        description = "Fruto amarelo-alaranjado piriforme de polpa doce e refrescante, muito comum em pomares do sul e sudeste."
    ),
    FruitCatalogItem(
        name = "Pequi",
        scientificName = "Caryocar brasiliense",
        family = "Caryocaraceae",
        biome = "Cerrado",
        category = "Cerrado & Caatinga",
        description = "O rei do cerrado! Fruto aromático de polpa amarelo-ouro que deve ser roída com cuidado devido aos espinhos internos."
    ),
    FruitCatalogItem(
        name = "Pinha",
        scientificName = "Annona squamosa",
        family = "Annonaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Muito próxima da fruta-do-conde, é apreciada por sua doçura suave e polpa cremosa que se desmancha na boca."
    ),
    FruitCatalogItem(
        name = "Pitaia",
        scientificName = "Selenicereus undatus",
        family = "Cactaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "A 'fruta-do-dragão' de cactos trepadores, com casca rosa exuberante, escamas verdes e polpa refrescante com sementinhas."
    ),
    FruitCatalogItem(
        name = "Pitanga",
        scientificName = "Eugenia uniflora",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Populares",
        description = "Baga sulcada com costelas avermelhadas brilhantes e sabor agridoce inconfundível, clássica de quintais e calçadas."
    ),
    FruitCatalogItem(
        name = "Pupunha",
        scientificName = "Bactris gasipaes",
        family = "Arecaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Frutos oleaginosos de palmeira consumidos cozidos com café, fundamentais na alimentação das populações amazônicas."
    ),
    FruitCatalogItem(
        name = "Sapota",
        scientificName = "Quararibea cordata",
        family = "Malvaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Sapota-do-solimões, grande fruto arredondado com casca parda e polpa alaranjada doce, fibrosa e muito saborosa."
    ),
    FruitCatalogItem(
        name = "Sapoti",
        scientificName = "Manilkara zapota",
        family = "Sapotaceae",
        biome = "Litoral",
        category = "Populares",
        description = "Fruto oval de casca marrom áspera e polpa incrivelmente doce que lembra mel e açúcar mascavo."
    ),
    FruitCatalogItem(
        name = "Seriguela",
        scientificName = "Spondias purpurea",
        family = "Anacardiaceae",
        biome = "Cerrado",
        category = "Populares",
        description = "Variação ortográfica da ciriguela, fruta de verão de casca fina avermelhada e polpa doce amarelada suculenta."
    ),
    FruitCatalogItem(
        name = "Taperebá",
        scientificName = "Spondias mombin",
        family = "Anacardiaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Denominação amazônica para o cajá, ingrediente nobre de sucos e sorvetes regionais do norte do país."
    ),
    FruitCatalogItem(
        name = "Tamarindo",
        scientificName = "Tamarindus indica",
        family = "Fabaceae",
        biome = "Cultivada",
        category = "Populares",
        description = "Vagem marrom rígida com polpa densa agridoce muito utilizada em sucos refrescantes, molhos e compotas."
    ),
    FruitCatalogItem(
        name = "Tucumã",
        scientificName = "Astrocaryum aculeatum",
        family = "Arecaceae",
        biome = "Amazônia",
        category = "Amazônia",
        description = "Fruto de palmeira amazônica com casca alaranjada e polpa amarela rica em ômega-3, famosa no sanduíche X-Caboquinho."
    ),
    FruitCatalogItem(
        name = "Uva",
        scientificName = "Vitis vinifera",
        family = "Vitaceae",
        biome = "Subtropical",
        category = "Populares",
        description = "Frutifica em cachos pesados com bagas doces ou viníferas, cultivada em parreirais urbanos e pomares familiares."
    ),
    FruitCatalogItem(
        name = "Uvaia",
        scientificName = "Eugenia pyriformis",
        family = "Myrtaceae",
        biome = "Mata Atlântica",
        category = "Mata Atlântica",
        description = "Fruto piriforme amarelo-dourado muito perfumado e aveludado, com polpa suculenta e sabor ácido-doce marcante."
    ),
    FruitCatalogItem(
        name = "Umbu",
        scientificName = "Spondias tuberosa",
        family = "Anacardiaceae",
        biome = "Caatinga",
        category = "Cerrado & Caatinga",
        description = "O 'sagrado do sertão', árvore da caatinga que armazena água nas raízes e produz frutinhos verdes doces e refrescantes."
    )
)
