#!/usr/bin/env bash
# This script is used to install pychron
# First in downloads and installs miniconda
# miniconda is used to manage the python dependencies
# conda is used to create a new environment
# conda and pip are used to install the dependencies
# a Pychron support directory is created and some boilerplate support files are written
# the pychron source code is downloaded from the available releases stored at github (i.e the source is not a git clone just a static directory)
# the source code is stored in the Pychron support directory
# a launcher script is created and copied to the desktop


# =========== Configuration ===============
WORKING_DIR=~/pychron_install_wd
CONDA_ENV=pychron
PYCHRONDATA_PREFIX=~/Pychron
DOWNLOAD_URL=https://github.com/NMGRL/pychron/archive/v3.tar.gz
MINICONDA_URL=https://repo.continuum.io/miniconda/Miniconda-latest-MacOSX-x86_64.sh
GITLFS_URL=https://github.com/github/git-lfs/releases/download/v1.1.0/git-lfs-darwin-amd64-1.1.0.tar.gz
MINICONDA_PREFIX=$HOME/miniconda2
MINICONDA_INSTALLER_SCRIPT=miniconda_installer.sh
LAUNCHER_SCRIPT_PATH=pychron_launcher.sh
ENTHOUGHT_DIR=$HOME/.enthought

CONDA_REQ="statsmodels
scikit-learn
PyYAML
traits
traitsui
chaco>
enable
pyface
envisage
sqlalchemy
Reportlab
lxml
xlrd
xlwt
pip
PySide
matplotlib
PyMySQL
requests
keyring
pil
paramiko"

PIP_REQ="uncertainties
pint
GitPython"

# =========== Payload text ===============
INITIALIZATION="<root>\n
  <globals>\n
  </globals>\n
  <plugins>\n
    <general>\n
      <plugin enabled='true'>ArArConstants</plugin>\n
      <plugin enabled='true'>DVC</plugin>\n
      <plugin enabled='true'>Pipeline</plugin>\n
    </general>\n
    <hardware>\n
    </hardware>\n
    <data>\n
    </data>\n
    <social>\n
      <plugin enabled='false'>Email</plugin>\n
      <plugin enabled='false'>Twitter</plugin>\n
    </social>\n
  </plugins>\n
</root>\n
"

DVC_PREFS="[pychron.dvc]\n
organization=NMGRL\n
meta_repo_name=meta\n
"

# =========== Setup Working dir ===========
cd

if ! [ -e ${WORKING_DIR} ]
 then
  echo Making working directory
  mkdir ${WORKING_DIR}
fi
cd ${WORKING_DIR}
# =========== Conda =======================

# check for conda
if type ${MINICONDA_PREFIX}/bin/conda >/dev/null
then

 # update conda
 echo conda already installed
 ${MINICONDA_PREFIX}/bin/conda update --yes conda
 echo Conda Updated

else
 echo conda doesnt exist
 # install conda

 # download miniconda installer script
 if ! [ -e ./${MINICONDA_INSTALLER_SCRIPT} ]
 then
  echo Downloading conda
  curl -L ${MINICONDA_URL} -o ${MINICONDA_INSTALLER_SCRIPT}
 fi

 chmod +x ./${MINICONDA_INSTALLER_SCRIPT}
 echo Installing conda. This may take a few minutes. Please be patient
 ./${MINICONDA_INSTALLER_SCRIPT} -b
 echo Conda Installed
 ${MINICONDA_PREFIX}/bin/conda update --yes conda
 echo Conda Updated
fi

${MINICONDA_PREFIX}/bin/conda create --yes -n${CONDA_ENV} pip

# install requirements
${MINICONDA_PREFIX}/envs/${CONDA_ENV}/bin/conda install -n${CONDA_ENV} --yes ${CONDA_REQ}
${MINICONDA_PREFIX}/envs/${CONDA_ENV}/bin/pip install ${PIP_REQ}


# make root
if [ -d ${PYCHRONDATA_PREFIX} ]
then
    echo ${PYCHRONDATA_PREFIX} already exists
else
    echo Making root directory ${PYCHRONDATA_PREFIX}
    mkdir ${PYCHRONDATA_PREFIX}
    mkdir ${PYCHRONDATA_PREFIX}/setupfiles
    mkdir ${PYCHRONDATA_PREFIX}/preferences

    printf ${DVC_PREFS} > ${PYCHRONDATA_PREFIX}/preferences/dvc.ini
    printf ${INITIALIZATION} > ${PYCHRONDATA_PREFIX}/setupfiles/initialization
    .xml
fi

# =========== Git LFS ===============

if type git lfs >/dev/null
then
    echo Git-LFS already installed
else
    echo Installing Git-LFS
    curl -L ${GITLFS_URL} -o git-lfs.tar.gz
    tar -xf ./git-lfs.tar.gz -C ./git-lfs --strip-components=1
    ./git-lfs/install.sh
fi

# =========== Unpack Release ===============
cd ${PYCHRONDATA_PREFIX}
mkdir ./src
curl -L ${DOWNLOAD_URL} -o pychron_src.tar.gz
tar -xf ./pychron_src.tar.gz -C ./src --strip-components=1

# ========== Launcher Script ===============
touch "${LAUNCHER_SCRIPT_PATH}"
printf ROOT=${PYCHRONDATA_PREFIX}/src > "${LAUNCHER_SCRIPT_PATH}"

printf ENTRY_POINT=\$ROOT/launchers/pyexperiment_debug.py >> "${LAUNCHER_SCRIPT_PATH}"
printf export PYTHONPATH=\$ROOT >> "${LAUNCHER_SCRIPT_PATH}"

printf ${MINICONDA_PREFIX}/envs/${CONDA_ENV}/bin/python \$ENTRY_POINT >> "${LAUNCHER_SCRIPT_PATH}"
chmod +x ${LAUNCHER_SCRIPT_PATH}
cp ${LAUNCHER_SCRIPT_PATH} ~/Desktop/

# ========= Enthought directory ============
if [ -d ${ENTHOUGHT_DIR} ]
then
    echo ${ENTHOUGHT_DIR} already exists
else
    echo Making root directory ${ENTHOUGHT_DIR}
    mkdir ${ENTHOUGHT_DIR}
fi

