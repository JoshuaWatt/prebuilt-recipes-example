# This is the normal recipe that projects will use (and also the default). It
# pulls pre-build packages from releases and integrates them into the build.
#
# If you get an error that the packages you want can't be downloaded from the
# server, you will need to ask someone with permission to generate new packages
SUMMARY = "Prebuilt Library"
LICENSE = "CLOSED"

DEPENDS = "\
    tar-native \
    xz-native \
    "

# Add the package arch to OVERRIDES so that the checksum variables can switch on it
OVERRIDES .= ":${PACKAGE_ARCH}"

PR = "r1"

S = "${WORKDIR}/${BP}"

URL_BASE = "http://my-site.com/prebuilt/${PACKAGE_ARCH}"
SRC_URI = "\
    ${URL_BASE}/libtest-dev_${PV}-${PR}_${PACKAGE_ARCH}.ipk;name=libtest-dev;subdir=${BP} \
    ${URL_BASE}/libtest1_${PV}-${PR}_${PACKAGE_ARCH}.ipk;name=libtest1;subdir=${BP} \
    "

SRC_URI[libtest-dev.md5sum] = "${MD5SUM_libtest-dev}"
SRC_URI[libtest1.md5sum] = "${MD5SUM_libtest1}"

PROVIDES = "virtual/libtest"

do_unpack[depends] += "xz-native:do_populate_sysroot"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    cd ${S} || exit 1
    cp -R --no-preserve=ownership * ${D}
}

# NOTE: The checksums below are automatically maintained by libtest-source_git.bb

MD5SUM_libtest-dbg_cortexa15hf-neon = "d1c0c9d0d61bb684087a5172d7199c68"
MD5SUM_libtest-dev_cortexa15hf-neon = "43285c88f56950255fa0b1dc52a4659c"
MD5SUM_libtest1_cortexa15hf-neon = "404c8be57955978fcafde1e8911b9080"
MD5SUM_libtest-dbg_aarch64 = "8aab9aa644fb60aab99a40a3290cab61"
MD5SUM_libtest-dev_aarch64 = "abefe1cb3ec72d8ea828e3785055267a"
MD5SUM_libtest1_aarch64 = "3b418fd217fb24b6b695317c48ae8ac0"
MD5SUM_libtest-dbg_i586 = "630272275a0fc4d2eda18d103939b232"
MD5SUM_libtest-dev_i586 = "f6eb2759016e23e586e4bdac2c11c61f"
MD5SUM_libtest1_i586 = "7b5abea2cd977bcbe7eb5a1f60de7c14"
MD5SUM_libtest-dbg_cortexa8hf-neon = "647c00163658a6f6f679cc5bc5fdf977"
MD5SUM_libtest-dev_cortexa8hf-neon = "93f94c172cd89c7b2478407501629564"
MD5SUM_libtest1_cortexa8hf-neon = "9298d63b338bd04d08fb51c2e69194b3"
MD5SUM_libtest-dbg_cortexa7t2hf-neon-vfpv4 = "6b94b1af6f266706add165072a2f8283"
MD5SUM_libtest-dev_cortexa7t2hf-neon-vfpv4 = "6e163a888149667cf16873ec00c19e32"
MD5SUM_libtest1_cortexa7t2hf-neon-vfpv4 = "7cddb0bab1fab2047aaf585bb344d8fd"
MD5SUM_libtest-dev_cortexa7hf-neon-vfpv4 = "4c492478c0aac00c03381cefe6bd12eb"
MD5SUM_libtest-dbg_cortexa7hf-neon-vfpv4 = "9e3f0d0d4963531ee38e96ff714a9b28"
MD5SUM_libtest1_cortexa7hf-neon-vfpv4 = "f817010dc3ba98cc030673daaaadd8a3"
MD5SUM_libtest1_core2-32 = "d2021ca7b2d4fc48e073224117225b0c"
MD5SUM_libtest-dev_core2-32 = "2b4256542b4067c402c866b7343e2598"
MD5SUM_libtest-dbg_core2-32 = "bafd94adae16ebe3d1a44ab9f4108726"
MD5SUM_libtest-dev_armv7vet2hf-neon = "a96853410266ee54f9881a04d4bd337b"
MD5SUM_libtest-dbg_armv7vet2hf-neon = "e921a019abeb8c9817d3ed47aea7aaad"
MD5SUM_libtest1_armv7vet2hf-neon = "c7f1757db34d846a18b720953c5003fb"
