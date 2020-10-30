# This recipe is used to build and upload the library packages to an HTTP
# server. Doing this requires permission to access the source code, as well as
# SSH privileges on the HTTP server
#
# To upload packages, follow these instuctions:
#  1. Clean out any old providers of the library:
#       bitbake -c cleanall virtual/libtest
#
#  2. Add the following to local.conf to instruct bitbake to use this recipe to
#     provide the library:
#       PREFERRED_PROVIDER_virtual/libtest = "libtest-source"
#
#  3. Update this recipe if necessary and commit the changes
#
#  4. Build the library from source:
#       bitbake virtual/libtest
#
#  5. Publish the build artifacts to releases:
#       bitbake -c publish virtual/libtest
#
#  6. The publish task will update the libtest-prebuilt recipe with new
#     variables/checksums. Commit the changes and push them up for review
#     to publish the library for general consumption.
#
#  7. If there are any API changes to the library, you may need to rebuild and
#     publish new SDKs in order for products to consume them.
#
# You may need to repeat steps 4-6 for multiple different machines to get the
# library published for all relevant architectures.
#
# If you would like to test changes to the library, you can combine this recipe
# with EXTERNALSRC, since it is a valid provider of the library.

SUMMARY = "Prebuilt Library"
LICENSE = "CLOSED"

PR = "r1"

SRC_URI = "git://my-library.git;protocol=ssh;branch=${BRANCH}"

BRANCH = "master"
SRCREV = "0000000000000000000000000000000000000000"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

PROVIDES = "virtual/libtest"
DEFAULT_PREFERENCE = "-1"

inherit pythonnative

EXTRA_OEMAKE = "BUILD_DIR=${B} BUILD_SHARED=1 BUILD_STATIC=0"

do_compile() {
    (cd ${S} && oe_runmake clean)
    (cd ${S} && oe_runmake)
}

do_install() {
    (cd ${S} && oe_runmake install DESTDIR=${D})
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

python () {
    # Always generate IPKs
    package_classes = d.getVar('PACKAGE_CLASSES').split()
    if 'package_ipk' not in package_classes:
        package_classes.append('package_ipk')
        d.setVar('PACKAGE_CLASSES', ' '.join(package_classes))
}

HOSTTOOLS += "ssh scp"

REMOTE_SERVER = "my-site.com"
REMOTE_PATH = "/var/lib/www/root/prebuilt/${PACKAGE_ARCH}"
PREBUILT_RECIPE_NAME = "libtest-prebuilt_git.bb"
PREBUILT_RECIPE = "${THISDIR}/${PREBUILT_RECIPE_NAME}"

set_recipe_var() {
    VAR="$1"
    VAL="$2"

    #bbplain "$VAR = \"$VAL\""
    if grep -q -F "$VAR " ${PREBUILT_RECIPE}; then
        sed -e "s/^$VAR .*/$VAR = \"$VAL\"/" \
            -i ${PREBUILT_RECIPE}
    else
        echo "$VAR = \"$VAL\"" >> ${PREBUILT_RECIPE}
    fi
}

do_publish() {
    ssh ${REMOTE_SERVER} 'mkdir -p ${REMOTE_PATH}'

    IPKS="$(find . -name '*.ipk')"

    for p in $IPKS; do
        BASENAME=$(basename $p)
        if ssh ${REMOTE_SERVER} "test -e ${REMOTE_PATH}/$BASENAME"; then
            bbfatal "$BASENAME already exists on the remote server, do you need to increment PR?"
        fi
    done

    for p in $IPKS; do
        scp $p '${REMOTE_SERVER}:${REMOTE_PATH}/'
    done

    for p in $IPKS; do
        PUBLISH_NAME=$(echo $(basename $p) | cut -f1 -d'_')
        MD5SUM="$(md5sum -b $p | cut -f1 -d' ')"

        MD5VARNAME="MD5SUM_${PUBLISH_NAME}_${PACKAGE_ARCH}"
        set_recipe_var "$MD5VARNAME" "$MD5SUM"
    done

    set_recipe_var "PR" "${PR}"

    bbplain "Changes made to ${PREBUILT_RECIPE_NAME}. Please commit them"
}

addtask publish after do_package_write_ipk
do_publish[dirs] = "${PKGWRITEDIRIPK}"
do_publish[nostamp] = "1"

