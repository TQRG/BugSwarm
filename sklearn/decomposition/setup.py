import os
import numpy

from sklearn._build_utils import get_blas_info


def configuration(parent_package='', top_path=None):
    from numpy.distutils.misc_util import Configuration

    config = Configuration('decomposition', parent_package, top_path)

    cblas_libs, blas_info = get_blas_info()

    if os.name == 'posix':
        cblas_libs.append('m')

    config.add_extension('cdnmf_fast',
                         sources=['cdnmf_fast.c'],
                         include_dirs=numpy.get_include())

    return config

if __name__ == '__main__':
    from numpy.distutils.core import setup
    setup(**configuration(top_path='').todict())
