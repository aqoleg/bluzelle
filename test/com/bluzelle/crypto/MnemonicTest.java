package com.bluzelle.crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MnemonicTest {

    @Test
    void generateMnemonic() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Mnemonic.generateMnemonic(127)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Mnemonic.generateMnemonic(288)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Mnemonic.generateMnemonic(135)
        );

        String mnemonic = Mnemonic.generateMnemonic(128);
        assertNotEquals(mnemonic, Mnemonic.generateMnemonic(128));
        Mnemonic.mnemonicToEntropy(mnemonic);
        Mnemonic.mnemonicToEntropy(Mnemonic.generateMnemonic(256));
    }

    @Test
    void entropy() {
        assertThrows(
                NullPointerException.class,
                () -> Mnemonic.entropyToMnemonic(null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Mnemonic.entropyToMnemonic(new byte[12])
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Mnemonic.entropyToMnemonic(new byte[36])
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Mnemonic.entropyToMnemonic(new byte[30])
        );

        assertThrows(
                NullPointerException.class,
                () -> Mnemonic.mnemonicToEntropy(null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Mnemonic.mnemonicToEntropy("noword")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Mnemonic.mnemonicToEntropy("zoo zoo")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Mnemonic.mnemonicToEntropy("legal winner thank year wave sausage worth useful legal winner "
                        + "thank zoo")
        );

        testEntropy(
                "00000000000000000000000000000000",
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
        );
        testEntropy(
                "7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f",
                "legal winner thank year wave sausage worth useful legal winner thank yellow"
        );
        testEntropy(
                "80808080808080808080808080808080",
                "letter advice cage absurd amount doctor acoustic avoid letter advice cage above"
        );
        testEntropy(
                "ffffffffffffffffffffffffffffffff",
                "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo wrong"
        );
        testEntropy(
                "000000000000000000000000000000000000000000000000",
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon "
                        + "abandon abandon abandon abandon abandon agent"
        );
        testEntropy(
                "7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f",
                "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful "
                        + "legal will"
        );
        testEntropy(
                "808080808080808080808080808080808080808080808080",
                "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor "
                        + "acoustic avoid letter always"
        );
        testEntropy(
                "ffffffffffffffffffffffffffffffffffffffffffffffff",
                "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo when"
        );
        testEntropy(
                "0000000000000000000000000000000000000000000000000000000000000000",
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon "
                        + "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon art"
        );
        testEntropy(
                "7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f",
                "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful "
                        + "legal winner thank year wave sausage worth title"
        );
        testEntropy(
                "8080808080808080808080808080808080808080808080808080808080808080",
                "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor "
                        + "acoustic avoid letter advice cage absurd amount doctor acoustic bless"
        );
        testEntropy(
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
                "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo vote"
        );
        testEntropy(
                "9e885d952ad362caeb4efe34a8e91bd2",
                "ozone drill grab fiber curtain grace pudding thank cruise elder eight picnic"
        );
        testEntropy(
                "6610b25967cdcca9d59875f5cb50b0ea75433311869e930b",
                "gravity machine north sort system female filter attitude volume fold club stay feature office "
                        + "ecology stable narrow fog"
        );
        testEntropy(
                "68a79eaca2324873eacc50cb9c6eca8cc68ea5d936f98787c60c7ebc74e6ce7c",
                "hamster diagram private dutch cause delay private meat slide toddler razor book happy fancy gospel "
                        + "tennis maple dilemma loan word shrug inflict delay length"
        );
        testEntropy(
                "c0ba5a8e914111210f2bd131f3d5e08d",
                "scheme spot photo card baby mountain device kick cradle pact join borrow"
        );
        testEntropy(
                "6d9be1ee6ebd27a258115aad99b7317b9c8d28b6d76431c3",
                "horn tenant knee talent sponsor spell gate clip pulse soap slush warm silver nephew swap uncle "
                        + "crack brave"
        );
        testEntropy(
                "9f6a2878b2520799a44ef18bc7df394e7061a224d2c33cd015b157d746869863",
                "panda eyebrow bullet gorilla call smoke muffin taste mesh discover soft ostrich alcohol speed nation "
                        + "flash devote level hobby quick inner drive ghost inside"
        );
        testEntropy(
                "23db8160a31d3e0dca3688ed941adbf3",
                "cat swing flag economy stadium alone churn speed unique patch report train"
        );
        testEntropy(
                "8197a4a47f0425faeaa69deebc05ca29c0a5b5cc76ceacc0",
                "light rule cinnamon wrap drastic word pride squirrel upgrade then income fatal apart sustain crack "
                        + "supply proud access"
        );
        testEntropy(
                "066dca1a2bb7e8a1db2832148ce9933eea0f3ac9548d793112d9a95c9407efad",
                "all hour make first leader extend hole alien behind guard gospel lava path output census museum "
                        + "junior mass reopen famous sing advance salt reform"
        );
        testEntropy(
                "f30f8c1da665478f49b001d94c5fc452",
                "vessel ladder alter error federal sibling chat ability sun glass valve picture"
        );
        testEntropy(
                "c10ec20dc3cd9f652c7fac2f1230f7a3c828389a14392f05",
                "scissors invite lock maple supreme raw rapid void congress muscle digital elegant little brisk hair "
                        + "mango congress clump"
        );
        testEntropy(
                "f585c11aec520db57dd353c69554b21a89b20fb0650966fa0a9d6f74fd989d8f",
                "void come effort suffer camp survey warrior heavy shoot primary clutch crush open amazing screen "
                        + "patrol group space point ten exist slush involve unfold"
        );
        testEntropy(
                "77c2b00716cec7213839159e404db50d",
                "jelly better achieve collect unaware mountain thought cargo oxygen act hood bridge"
        );
        testEntropy(
                "b63a9c59a6e641f288ebc103017f1da9f8290b3da6bdef7b",
                "renew stay biology evidence goat welcome casual join adapt armor shuffle fault little machine walk "
                        + "stumble urge swap"
        );
        testEntropy(
                "3e141609b97933b66a060dcddc71fad1d91677db872031e85f4c015c5e7e8982",
                "dignity pass list indicate nasty swamp pool script soccer toe leaf photo multiply desk host tomato "
                        + "cradle drill spread actor shine dismiss champion exotic"
        );
        testEntropy(
                "0460ef47585604c5660618db2e6a7e7f",
                "afford alter spike radar gate glance object seek swamp infant panel yellow"
        );
        testEntropy(
                "72f60ebac5dd8add8d2a25a797102c3ce21bc029c200076f",
                "indicate race push merry suffer human cruise dwarf pole review arch keep canvas theme poem divorce "
                        + "alter left"
        );
        testEntropy(
                "2c85efc7f24ee4573d2b81a6ec66cee209b2dcbd09d8eddc51e0215b0b68e416",
                "clutch control vehicle tonight unusual clog visa ice plunge glimpse recipe series open hour vintage "
                        + "deposit universe tip job dress radar refuse motion taste"
        );
        testEntropy(
                "eaebabb2383351fd31d703840b32e9e2",
                "turtle front uncle idea crush write shrug there lottery flower risk shell"
        );
        testEntropy(
                "7ac45cfe7722ee6c7ba84fbc2d5bd61b45cb2fe5eb65aa78",
                "kiss carry display unusual confirm curtain upgrade antique rotate hello void custom frequent obey nut "
                        + "hole price segment"
        );
        testEntropy(
                "4fa1a8bc3e6d80ee1316050e862c1812031493212b7ec3f3bb1b08f168cabeef",
                "exile ask congress lamp submit jacket era scheme attend cousin alcohol catch course end lucky hurt "
                        + "sentence oven short ball bird grab wing top"
        );
        testEntropy(
                "18ab19a9f54a9274f03e5209a2ac8a91",
                "board flee heavy tunnel powder denial science ski answer betray cargo cat"
        );
        testEntropy(
                "18a2e1d81b8ecfb2a333adcb0c17a5b9eb76cc5d05db91a4",
                "board blade invite damage undo sun mimic interest slam gaze truly inherit resist great inject rocket "
                        + "museum chief"
        );
        testEntropy(
                "15da872c95a13dd738fbf50e427583ad61f18fd99f628c417a61cf8343c90419",
                "beyond stage sleep clip because twist token leaf atom beauty genius food business side grid unable "
                        + "middle armed observe pair crouch tonight away coconut"
        );
    }

    @Test
    void createSeed() {
        assertThrows(
                NullPointerException.class,
                () -> Mnemonic.createSeed(null, "")
        );
        assertThrows(
                NullPointerException.class,
                () -> Mnemonic.createSeed("mnemonic", null)
        );

        areSeedEquals(
                "wild father tree among universe such mobile favorite target dynamic credit identify",
                "electrum",
                "aac2a6302e48577ab4b46f23dbae0774e2e62c796f797d0a1b5faeb528301e3064342dafb79069e7c4c6b8c38ae11d7a97"
                        + "3bec0d4f70626f8cc5184a8d0b0756"
        );
        areSeedEquals(
                "foobar",
                "electrum" + "none",
                "741b72fd15effece6bfe5a26a52184f66811bd2be363190e07a42cca442b1a5bb22b3ad0eb338197287e6d314866c7fba8"
                        + "63ac65d3f156087a5052ebc7157fce"
        );
        areSeedEquals(
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about",
                "mnemonic" + "TREZOR",
                "c55257c360c07c72029aebc1b53c05ed0362ada38ead3e3e9efa3708e53495531f09a6987599d18264c1e1c92f2cf14163"
                        + "0c7a3c4ab7c81b2f001698e7463b04"
        );
        areSeedEquals(
                "legal winner thank year wave sausage worth useful legal winner thank yellow",
                "mnemonic" + "TREZOR",
                "2e8905819b8723fe2c1d161860e5ee1830318dbf49a83bd451cfb8440c28bd6fa457fe1296106559a3c80937a1c1069be3"
                        + "a3a5bd381ee6260e8d9739fce1f607"
        );
        areSeedEquals(
                "letter advice cage absurd amount doctor acoustic avoid letter advice cage above",
                "mnemonic" + "TREZOR",
                "d71de856f81a8acc65e6fc851a38d4d7ec216fd0796d0a6827a3ad6ed5511a30fa280f12eb2e47ed2ac03b5c462a0358d1"
                        + "8d69fe4f985ec81778c1b370b652a8"
        );
        areSeedEquals(
                "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo wrong",
                "mnemonic" + "TREZOR",
                "ac27495480225222079d7be181583751e86f571027b0497b5b5d11218e0a8a13332572917f0f8e5a589620c6f15b11c61d"
                        + "ee327651a14c34e18231052e48c069"
        );
        areSeedEquals(
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon"
                        + " abandon abandon abandon abandon abandon agent",
                "mnemonic" + "TREZOR",
                "035895f2f481b1b0f01fcf8c289c794660b289981a78f8106447707fdd9666ca06da5a9a565181599b79f53b844d8a71dd"
                        + "9f439c52a3d7b3e8a79c906ac845fa"
        );
        areSeedEquals(
                "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful"
                        + " legal will",
                "mnemonic" + "TREZOR",
                "f2b94508732bcbacbcc020faefecfc89feafa6649a5491b8c952cede496c214a0c7b3c392d168748f2d4a612bada0753b5"
                        + "2a1c7ac53c1e93abd5c6320b9e95dd"
        );
        areSeedEquals(
                "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor"
                        + " acoustic avoid letter always",
                "mnemonic" + "TREZOR",
                "107d7c02a5aa6f38c58083ff74f04c607c2d2c0ecc55501dadd72d025b751bc27fe913ffb796f841c49b1d33b610cf0e91"
                        + "d3aa239027f5e99fe4ce9e5088cd65"
        );
        areSeedEquals(
                "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo when",
                "mnemonic" + "TREZOR",
                "0cd6e5d827bb62eb8fc1e262254223817fd068a74b5b449cc2f667c3f1f985a76379b43348d952e2265b4cd129090758b3"
                        + "e3c2c49103b5051aac2eaeb890a528"
        );
        areSeedEquals(
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon"
                        + " abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon art",
                "mnemonic" + "TREZOR",
                "bda85446c68413707090a52022edd26a1c9462295029f2e60cd7c4f2bbd3097170af7a4d73245cafa9c3cca8d561a7c3de"
                        + "6f5d4a10be8ed2a5e608d68f92fcc8"
        );
        areSeedEquals(
                "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful"
                        + " legal winner thank year wave sausage worth title",
                "mnemonic" + "TREZOR",
                "bc09fca1804f7e69da93c2f2028eb238c227f2e9dda30cd63699232578480a4021b146ad717fbb7e451ce9eb835f43620b"
                        + "f5c514db0f8add49f5d121449d3e87"
        );
        areSeedEquals(
                "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor"
                        + " acoustic avoid letter advice cage absurd amount doctor acoustic bless",
                "mnemonic" + "TREZOR",
                "c0c519bd0e91a2ed54357d9d1ebef6f5af218a153624cf4f2da911a0ed8f7a09e2ef61af0aca007096df430022f7a2b6fb"
                        + "91661a9589097069720d015e4e982f"
        );
        areSeedEquals(
                "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo vote",
                "mnemonic" + "TREZOR",
                "dd48c104698c30cfe2b6142103248622fb7bb0ff692eebb00089b32d22484e1613912f0a5b694407be899ffd31ed3992c4"
                        + "56cdf60f5d4564b8ba3f05a69890ad"
        );
        areSeedEquals(
                "ozone drill grab fiber curtain grace pudding thank cruise elder eight picnic",
                "mnemonic" + "TREZOR",
                "274ddc525802f7c828d8ef7ddbcdc5304e87ac3535913611fbbfa986d0c9e5476c91689f9c8a54fd55bd38606aa6a8595a"
                        + "d213d4c9c9f9aca3fb217069a41028"
        );
        areSeedEquals(
                "gravity machine north sort system female filter attitude volume fold club stay feature office ecology"
                        + " stable narrow fog",
                "mnemonic" + "TREZOR",
                "628c3827a8823298ee685db84f55caa34b5cc195a778e52d45f59bcf75aba68e4d7590e101dc414bc1bbd5737666fbbef3"
                        + "5d1f1903953b66624f910feef245ac"
        );
        areSeedEquals(
                "hamster diagram private dutch cause delay private meat slide toddler razor book happy fancy gospel"
                        + " tennis maple dilemma loan word shrug inflict delay length",
                "mnemonic" + "TREZOR",
                "64c87cde7e12ecf6704ab95bb1408bef047c22db4cc7491c4271d170a1b213d20b385bc1588d9c7b38f1b39d415665b8a9"
                        + "030c9ec653d75e65f847d8fc1fc440"
        );
        areSeedEquals(
                "scheme spot photo card baby mountain device kick cradle pact join borrow",
                "mnemonic" + "TREZOR",
                "ea725895aaae8d4c1cf682c1bfd2d358d52ed9f0f0591131b559e2724bb234fca05aa9c02c57407e04ee9dc3b454aa63fb"
                        + "ff483a8b11de949624b9f1831a9612"
        );
        areSeedEquals(
                "horn tenant knee talent sponsor spell gate clip pulse soap slush warm silver nephew swap uncle crack"
                        + " brave",
                "mnemonic" + "TREZOR",
                "fd579828af3da1d32544ce4db5c73d53fc8acc4ddb1e3b251a31179cdb71e853c56d2fcb11aed39898ce6c34b10b538277"
                        + "2db8796e52837b54468aeb312cfc3d"
        );
        areSeedEquals(
                "panda eyebrow bullet gorilla call smoke muffin taste mesh discover soft ostrich alcohol speed nation"
                        + " flash devote level hobby quick inner drive ghost inside",
                "mnemonic" + "TREZOR",
                "72be8e052fc4919d2adf28d5306b5474b0069df35b02303de8c1729c9538dbb6fc2d731d5f832193cd9fb6aeecbc469594"
                        + "a70e3dd50811b5067f3b88b28c3e8d"
        );
        areSeedEquals(
                "cat swing flag economy stadium alone churn speed unique patch report train",
                "mnemonic" + "TREZOR",
                "deb5f45449e615feff5640f2e49f933ff51895de3b4381832b3139941c57b59205a42480c52175b6efcffaa58a2503887c"
                        + "1e8b363a707256bdd2b587b46541f5"
        );
        areSeedEquals(
                "light rule cinnamon wrap drastic word pride squirrel upgrade then income fatal apart sustain crack"
                        + " supply proud access",
                "mnemonic" + "TREZOR",
                "4cbdff1ca2db800fd61cae72a57475fdc6bab03e441fd63f96dabd1f183ef5b782925f00105f318309a7e9c3ea6967c780"
                        + "1e46c8a58082674c860a37b93eda02"
        );
        areSeedEquals(
                "all hour make first leader extend hole alien behind guard gospel lava path output census museum "
                        + "junior mass reopen famous sing advance salt reform",
                "mnemonic" + "TREZOR",
                "26e975ec644423f4a4c4f4215ef09b4bd7ef924e85d1d17c4cf3f136c2863cf6df0a475045652c57eb5fb41513ca2a2d67"
                        + "722b77e954b4b3fc11f7590449191d"
        );
        areSeedEquals(
                "vessel ladder alter error federal sibling chat ability sun glass valve picture",
                "mnemonic" + "TREZOR",
                "2aaa9242daafcee6aa9d7269f17d4efe271e1b9a529178d7dc139cd18747090bf9d60295d0ce74309a78852a9caadf0af4"
                        + "8aae1c6253839624076224374bc63f"
        );
        areSeedEquals(
                "scissors invite lock maple supreme raw rapid void congress muscle digital elegant little brisk hair "
                        + "mango congress clump",
                "mnemonic" + "TREZOR",
                "7b4a10be9d98e6cba265566db7f136718e1398c71cb581e1b2f464cac1ceedf4f3e274dc270003c670ad8d02c4558b2f8e"
                        + "39edea2775c9e232c7cb798b069e88"
        );
        areSeedEquals(
                "void come effort suffer camp survey warrior heavy shoot primary clutch crush open amazing screen "
                        + "patrol group space point ten exist slush involve unfold",
                "mnemonic" + "TREZOR",
                "01f5bced59dec48e362f2c45b5de68b9fd6c92c6634f44d6d40aab69056506f0e35524a518034ddc1192e1dacd32c1ed3e"
                        + "aa3c3b131c88ed8e7e54c49a5d0998"
        );
        areSeedEquals(
                "memory coach exit",
                "mnemonic",
                "7dc61341dd16c16dcd2d201d77b23874841195cc1886e92a325d4e5a11bd287caf6c23ca5e63c8eb831810b8b250a975e2"
                        + "2a872dae309cb7e8727f1c11ddd0ce"
        );
        areSeedEquals(
                "identify piece abstract dinner spell cash trash gather beef harbor opera elite ivory absorb glide "
                        + "toast lock river leopard scorpion parrot pretty siege try",
                "mnemonic" + "identify piece abstract dinner spell cash trash gather beef harbor opera elite ivory "
                        + "absorb glide toast lock river leopard scorpion parrot pretty siege try",
                "248340b42d9d49f19c4e62b087dff0d44765ea1c34abe2467a1edf63567d7f5917d8374fb7460dcd560a19f5be35d6438d"
                        + "a4deded6dc3e477b489a2ea7d183b9"
        );
        areSeedEquals(
                "acquire address mirror check ceiling salt",
                "mnemonic",
                "f46dda165b2068a91106a15980a8550d510685a012c49b0eea13056de5143f58436fce0c7b8f5dc5e84892194a3eb711f9"
                        + "268ca12b9b59607acee1627dbaa56c"
        );
        areSeedEquals(
                "elegant aunt snow squeeze opera then surface start action dad logic three",
                "mnemonic",
                "84fbe72835916fd3bc12e209ba30f01468dd8fadfb3b120c8e40be36fbe4f3d716124bb63f1c0d94caa245c202fcc68c82"
                        + "cbbdf30afc1ef96e15299ad63315a7"
        );
        areSeedEquals(
                "danger agree whip",
                "mnemonic",
                "9be8cb93ff66226d6cf478670e3bb21fbe38e1adc6b710625f642de5385d71b16be9421f437d1ee3626bf30a7298d45903"
                        + "8d74221f06f7521daecfa2ceb262f9"
        );
        areSeedEquals(
                "basket actual",
                "mnemonic",
                "5cf2d4a8b0355e90295bdfc565a022a409af063d5365bb57bf74d9528f494bfa4400f53d8349b80fdae44082d7f9541e1d"
                        + "ba2b003bcfec9d0d53781ca676651f"
        );
        areSeedEquals(
                "dignity pass list indicate nasty swamp pool script soccer toe leaf photo multiply desk host tomato "
                        + "cradle drill spread actor shine dismiss champion exotic",
                "mnemonic" + "TREZOR",
                "ff7f3184df8696d8bef94b6c03114dbee0ef89ff938712301d27ed8336ca89ef9635da20af07d4175f2bf5f3de130f39c9"
                        + "d9e8dd0472489c19b1a020a940da67"
        );
        areSeedEquals(
                "afford alter spike radar gate glance object seek swamp infant panel yellow",
                "mnemonic" + "TREZOR",
                "65f93a9f36b6c85cbe634ffc1f99f2b82cbb10b31edc7f087b4f6cb9e976e9faf76ff41f8f27c99afdf38f7a303ba1136e"
                        + "e48a4c1e7fcd3dba7aa876113a36e4"
        );
        areSeedEquals(
                "indicate race push merry suffer human cruise dwarf pole review arch keep canvas theme poem divorce "
                        + "alter left",
                "mnemonic" + "TREZOR",
                "3bbf9daa0dfad8229786ace5ddb4e00fa98a044ae4c4975ffd5e094dba9e0bb289349dbe2091761f30f382d4e35c4a670e"
                        + "e8ab50758d2c55881be69e327117ba"
        );
        areSeedEquals(
                "clutch control vehicle tonight unusual clog visa ice plunge glimpse recipe series open hour vintage "
                        + "deposit universe tip job dress radar refuse motion taste",
                "mnemonic" + "TREZOR",
                "fe908f96f46668b2d5b37d82f558c77ed0d69dd0e7e043a5b0511c48c2f1064694a956f86360c93dd04052a8899497ce9e"
                        + "985ebe0c8c52b955e6ae86d4ff4449"
        );
        areSeedEquals(
                "turtle front uncle idea crush write shrug there lottery flower risk shell",
                "mnemonic" + "TREZOR",
                "bdfb76a0759f301b0b899a1e3985227e53b3f51e67e3f2a65363caedf3e32fde42a66c404f18d7b05818c95ef3ca1e5146"
                        + "646856c461c073169467511680876c"
        );
        areSeedEquals(
                "kiss carry display unusual confirm curtain upgrade antique rotate hello void custom frequent obey "
                        + "nut hole price segment",
                "mnemonic" + "TREZOR",
                "ed56ff6c833c07982eb7119a8f48fd363c4a9b1601cd2de736b01045c5eb8ab4f57b079403485d1c4924f0790dc10a9717"
                        + "63337cb9f9c62226f64fff26397c79"
        );
        areSeedEquals(
                "exile ask congress lamp submit jacket era scheme attend cousin alcohol catch course end lucky hurt "
                        + "sentence oven short ball bird grab wing top",
                "mnemonic" + "TREZOR",
                "095ee6f817b4c2cb30a5a797360a81a40ab0f9a4e25ecd672a3f58a0b5ba0687c096a6b14d2c0deb3bdefce4f61d01ae07"
                        + "417d502429352e27695163f7447a8c"
        );
        areSeedEquals(
                "board flee heavy tunnel powder denial science ski answer betray cargo cat",
                "mnemonic" + "TREZOR",
                "6eff1bb21562918509c73cb990260db07c0ce34ff0e3cc4a8cb3276129fbcb300bddfe005831350efd633909f476c45c88"
                        + "253276d9fd0df6ef48609e8bb7dca8"
        );
        areSeedEquals(
                "board blade invite damage undo sun mimic interest slam gaze truly inherit resist great inject rocket "
                        + "museum chief",
                "mnemonic" + "TREZOR",
                "f84521c777a13b61564234bf8f8b62b3afce27fc4062b51bb5e62bdfecb23864ee6ecf07c1d5a97c0834307c5c852d8ceb"
                        + "88e7c97923c0a3b496bedd4e5f88a9"
        );
        areSeedEquals(
                "beyond stage sleep clip because twist token leaf atom beauty genius food business side grid unable "
                        + "middle armed observe pair crouch tonight away coconut",
                "mnemonic" + "TREZOR",
                "b15509eaa2d09d3efd3e006ef42151b30367dc6e3aa5e44caba3fe4d3e352e65101fbdb86a96776b91946ff06f8eac594d"
                        + "c6ee1d3e82a42dfe1b40fef6bcc3fd"
        );
    }

    private void testEntropy(String entropyHex, String mnemonic) {
        byte[] entropy = new byte[entropyHex.length() / 2];
        for (int i = 0; i < entropyHex.length(); i += 2) {
            int firstChar = Character.digit(entropyHex.charAt(i), 16);
            int secondChar = Character.digit(entropyHex.charAt(i + 1), 16);
            entropy[i / 2] = (byte) (firstChar << 4 | secondChar);
        }
        assertEquals(mnemonic, Mnemonic.entropyToMnemonic(entropy));
        assertArrayEquals(entropy, Mnemonic.mnemonicToEntropy(mnemonic));
    }

    private void areSeedEquals(String mnemonic, String passphrase, String correctSeedHex) {
        byte[] seed = Mnemonic.createSeed(mnemonic, passphrase);
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : seed) {
            stringBuilder.append(String.format("%02x", b));
        }
        assertEquals(correctSeedHex, stringBuilder.toString());
    }
}