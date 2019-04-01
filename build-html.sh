rm -rf build
make SPHINXOPTS="-c conf/html" html

cat $HOME/tmp/sphinx/build-error.log