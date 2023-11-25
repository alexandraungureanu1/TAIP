def upper_no_diacritics(text):
    return text.upper().replace('Ș', 'S').replace('Ă', 'A').replace('Â', 'A').replace('Î', 'I').replace('Ț', 'T')


CNP_SERIES = [
    'AX', 'TR', 'AR', 'ZR', 'XC', 'ZC', 'MM', 'XM', 'XB', 'XT', 'BV', 'ZV', 'XR', 'DP', 'DR', 'DT',
    'DX', 'RD', 'RR', 'RT', 'RX', 'RK', 'IF', 'XZ', 'KL', 'KX', 'CJ', 'KT', 'KZ', 'DX', 'DZ', 'HD',
    'VN', 'GL', 'ZL', 'GG', 'MX', 'MZ', 'MH', 'HR', 'XH', 'ZH', 'NT', 'AS', 'AZ', 'PH', 'PX', 'KS',
    'VX', 'SM', 'KV', 'SB', 'OT', 'SZ', 'SV', 'XV', 'TM', 'TZ', 'DD', 'GZ', 'ZS', 'MS', 'TC', 'VS', 'SX'
]
CNP_SERIES_REGEX = '|'.join(CNP_SERIES)

CITIES = [
    'Abrud', 'Adjud', 'Agnita', 'Aiud', 'Alba Iulia', 'Aleșd', 'Alexandria', 'Amara', 'Anina', 'Aninoasa', 'Arad',
    'Ardud', 'Avrig', 'Azuga', 'Babadag', 'Băbeni', 'Bacău', 'Baia de Aramă', 'Baia de Arieș', 'Baia Mare',
    'Baia Sprie',
    'Băicoi', 'Băile Govora', 'Băile Herculane', 'Băile Olănești', 'Băile Tușnad', 'Băilești', 'Bălan', 'Bălcești',
    'Balș', 'Baraolt', 'Bârlad', 'Bechet', 'Beclean', 'Beiuș', 'Berbești', 'Berești', 'Bicaz', 'Bistrița', 'Blaj',
    'Bocșa', 'Boldești-Scăeni', 'Bolintin-Vale', 'Borșa', 'Borsec', 'Botoșani', 'Brad', 'Bragadiru', 'Brăila', 'Brașov',
    'Breaza', 'Brezoi', 'Broșteni', 'Bucecea', 'București', 'Budești', 'Buftea', 'Buhuși', 'Bumbești-Jiu', 'Bușteni',
    'Buzău', 'Buziaș', 'Cajvana', 'Calafat', 'Călan', 'Călărași', 'Călimănești', 'Câmpeni', 'Câmpia Turzii', 'Câmpina',
    'Câmpulung Moldovenesc', 'Câmpulung', 'Caracal', 'Caransebeș', 'Carei', 'Cavnic', 'Căzănești', 'Cehu Silvaniei',
    'Cernavodă', 'Chișineu-Criș', 'Chitila', 'Ciacova', 'Cisnădie', 'Cluj-Napoca', 'Codlea', 'Comănești', 'Comarnic',
    'Constanța', 'Copșa Mică', 'Corabia', 'Costești', 'Covasna', 'Craiova', 'Cristuru Secuiesc', 'Cugir',
    'Curtea de Argeș', 'Curtici', 'Dăbuleni', 'Darabani', 'Dărmănești', 'Dej', 'Deta', 'Deva', 'Dolhasca', 'Dorohoi',
    'Drăgănești-Olt', 'Drăgășani', 'Dragomirești', 'Drobeta-Turnu Severin', 'Dumbrăveni', 'Eforie', 'Făgăraș', 'Făget',
    'Fălticeni', 'Făurei', 'Fetești', 'Fieni', 'Fierbinți-Târg', 'Filiași', 'Flămânzi', 'Focșani', 'Frasin', 'Fundulea',
    'Găești', 'Galați', 'Gătaia', 'Geoagiu', 'Gheorgheni', 'Gherla', 'Ghimbav', 'Giurgiu', 'Gura Humorului', 'Hârlău',
    'Hârșova', 'Hațeg', 'Horezu', 'Huedin', 'Hunedoara', 'Huși', 'Ianca', 'Iași', 'Iernut', 'Ineu', 'Însurăței',
    'Întorsura Buzăului', 'Isaccea', 'Jibou', 'Jimbolia', 'Lehliu Gară', 'Lipova', 'Liteni', 'Livada', 'Luduș', 'Lugoj',
    'Lupeni', 'Măcin', 'Măgurele', 'Mangalia', 'Mărășești', 'Marghita', 'Medgidia', 'Mediaș', 'Miercurea Ciuc',
    'Miercurea Nirajului', 'Miercurea Sibiului', 'Mihăilești', 'Milișăuți', 'Mioveni', 'Mizil', 'Moinești',
    'Moldova Nouă', 'Moreni', 'Motru', 'Murfatlar', 'Murgeni', 'Nădlac', 'Năsăud', 'Năvodari', 'Negrești',
    'Negrești-Oaș', 'Negru Vodă', 'Nehoiu', 'Novaci', 'Nucet', 'Ocna Mureș', 'Ocna Sibiului', 'Ocnele Mari', 'Odobești',
    'Odorheiu Secuiesc', 'Oltenița', 'Onești', 'Oradea', 'Orăștie', 'Oravița', 'Orșova', 'Oțelu Roșu', 'Otopeni',
    'Ovidiu', 'Panciu', 'Pâncota', 'Pantelimon', 'Pașcani', 'Pătârlagele', 'Pecica', 'Petrila', 'Petroșani',
    'Piatra Neamț', 'Piatra-Olt', 'Pitești', 'Ploiești', 'Plopeni', 'Podu Iloaiei', 'Pogoanele', 'Popești-Leordeni',
    'Potcoava', 'Predeal', 'Pucioasa', 'Răcari', 'Rădăuți', 'Râmnicu Sărat', 'Râmnicu Vâlcea', 'Râșnov', 'Recaș',
    'Reghin', 'Reșița', 'Roman', 'Roșiorii de Vede', 'Rovinari', 'Roznov', 'Rupea', 'Săcele', 'Săcueni', 'Salcea',
    'Săliște', 'Săliștea de Sus', 'Salonta', 'Sângeorgiu de Pădure', 'Sângeorz-Băi', 'Sânnicolau Mare', 'Sântana',
    'Sărmașu', 'Satu Mare', 'Săveni', 'Scornicești', 'Sebeș', 'Sebiș', 'Segarcea', 'Seini', 'Sfântu Gheorghe', 'Sibiu',
    'Sighetu Marmației', 'Sighișoara', 'Simeria', 'Șimleu Silvaniei', 'Sinaia', 'Siret', 'Slănic', 'Slănic-Moldova',
    'Slatina', 'Slobozia', 'Solca', 'Șomcuta Mare', 'Sovata', 'Ștefănești', 'Ștei', 'Strehaia', 'Suceava', 'Sulina',
    'Tălmaciu', 'Țăndărei', 'Târgoviște', 'Târgu Bujor', 'Târgu Cărbunești', 'Târgu Frumos', 'Târgu Jiu', 'Târgu Lăpuș',
    'Târgu Mureș', 'Târgu Neamț', 'Târgu Ocna', 'Târgu Secuiesc', 'Târnăveni', 'Tășnad', 'Tăuții-Măgherăuș',
    'Techirghiol', 'Tecuci', 'Teiuș', 'Țicleni', 'Timișoara', 'Tismana', 'Titu', 'Toplița', 'Topoloveni', 'Tulcea',
    'Turceni', 'Turda', 'Turnu Măgurele', 'Ulmeni', 'Ungheni', 'Uricani', 'Urlați', 'Urziceni', 'Valea lui Mihai',
    'Vălenii de Munte', 'Vânju Mare', 'Vașcău', 'Vaslui', 'Vatra Dornei', 'Vicovu de Sus', 'Victoria', 'Videle',
    'Vișeu de Sus', 'Vlăhița', 'Voluntari', 'Vulcan', 'Zalău', 'Zărnești', 'Zimnicea', 'Zlatna'
]
CITIES_REGEX = '|'.join(CITIES)
CITIES_UPPER_NO_DIACRITICS_REGEX = '|'.join([upper_no_diacritics(city) for city in CITIES])

MUNICIPALITIES = [
    'Alba Iulia', 'Aiud', 'Blaj', 'Sebeș', 'Arad', 'Pitești', 'Câmpulung - Muscel', 'Curtea de Argeș', 'Onești',
    'Moinești', 'Beiuș', 'Marghita', 'Salonta', 'Bistrița', 'Botoșani', 'Dorohoi', 'Făgăraș', 'Codlea',
    'Săcele', 'Buzău', 'Râmnicu Sărat', 'Reșița', 'Caransebeș', 'Călărași', 'Oltenița', 'Turda', 'Dej',
    'Câmpia Turzii', 'Gherla', 'Mangalia', 'Medgidia', 'Sfântu Gheorghe', 'Târgu Secuiesc', 'Târgoviște',
    'Moreni', 'Calafat', 'Băilești', 'Tecuci', 'Giurgiu', 'Târgu Jiu', 'Motru', 'Miercurea Ciuc',
    'Gheorgheni', 'Odorheiu Secuiesc', 'Toplița', 'Deva', 'Hunedoara', 'Brad', 'Lupeni', 'Orăștie',
    'Petroșani', 'Vulcan', 'Slobozia', 'Fetești', 'Urziceni', 'Pașcani', 'Baia Mare', 'Sighetu Marmației',
    'Drobeta - Turnu Severin', 'Orșova', 'Târgu Mureș', 'Sighișoara', 'Reghin', 'Târnăveni', 'Piatra Neamț',
    'Roman', 'Slatina', 'Caracal', 'Câmpina', 'Satu Mare', 'Carei', 'Zalău', 'Sibiu', 'Mediaș', 'Suceava',
    'Fălticeni', 'Rădăuți', 'Câmpulung Moldovenesc', 'Vatra Dornei', 'Alexandria', 'Roșiori de Vede',
    'Turnu Măgurele', 'Lugoj', 'Tulcea', 'Vaslui', 'Bârlad', 'Huși', 'Râmnicu Vâlcea', 'Drăgășani',
    'Focșani', 'Adjud'
]
MUNICIPALITIES_REGEX = '|'.join(MUNICIPALITIES)

DISTRICTS_FULL = [
    'Alba', 'Arad', 'Argeș', 'Bacău', 'Bihor', 'Bistrița-Năsăud', 'Botoșani', 'Brașov', 'Brăila', 'București', 'Buzău',
    'Caraș-Severin',
    'Călărași', 'Cluj', 'Constanța', 'Covasna', 'Dâmbovița', 'Dolj', 'Galați', 'Giurgiu', 'Gorj', 'Harghita',
    'Hunedoara',
    'Ialomița', 'Iași', 'Ilfov', 'Maramureș', 'Mehedinți', 'Mureș', 'Neamț', 'Olt', 'Prahova', 'Satu Mare', 'Sălaj',
    'Sibiu',
    'Suceava', 'Teleorman', 'Timiș', 'Tulcea', 'Vaslui', 'Vâlcea', 'Vrancea'
]
DISTRICTS_FULL_REGEX = '|'.join(DISTRICTS_FULL)
DISTRICTS_UPPER_NO_DIACRITICS_REGEX = '|'.join([upper_no_diacritics(district) for district in DISTRICTS_FULL])

DISTRICTS_SHORT = [
    'AB', 'AR', 'AG', 'B', 'BC', 'BH', 'BN', 'BT', 'BV', 'BR', 'BZ', 'CS', 'CL', 'CJ', 'CT', 'CV', 'DB', 'DJ', 'GL',
    'GR', 'GJ', 'HR', 'HD', 'IL', 'IS', 'IF', 'MM', 'MH', 'MS', 'NT', 'OT', 'PH', 'SM', 'SJ', 'SB', 'SV', 'TR',
    'TM', 'TL', 'VS', 'VL', 'VN'
]
DISTRICTS_SHORT_REGEX = '|'.join(DISTRICTS_SHORT)

AUTO_CATEGORIES = [
    'AM', 'A1', 'A2', 'A', 'BE', 'B1', 'B', 'C1E', 'CE', 'C1', 'C', 'D1E', 'DE', 'D1', 'D', 'Tr', 'Tv', 'Tb'
]
AUTO_CATEGORIES_REGEX = '|'.join(AUTO_CATEGORIES)
