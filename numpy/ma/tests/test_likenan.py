from __future__ import division, absolute_import, print_function

import warnings

import numpy as np
from numpy.testing import (
    run_module_suite, TestCase, assert_, assert_equal, assert_almost_equal,
    assert_raises, assert_array_equal
    )


# Test data
_ndat = np.array([[0.6244, np.nan, 0.2692, 0.0116, np.nan, 0.1170],
                  [0.5351, -0.9403, np.nan, 0.2100, 0.4759, 0.2833],
                  [np.nan, np.nan, np.nan, 0.1042, np.nan, -0.5954],
                  [0.1610, np.nan, np.nan, 0.1859, 0.3146, np.nan]])

_madat = np.ma.array(_ndat, mask=np.isnan(_ndat))

# Rows of _ndat with nans removed
_rdat = [np.array([0.6244, 0.2692, 0.0116, 0.1170]),
         np.array([0.5351, -0.9403, 0.2100, 0.4759, 0.2833]),
         np.array([0.1042, -0.5954]),
         np.array([0.1610, 0.1859, 0.3146])]


class TestMaskedFunctions_MinMax(TestCase):

    mafuncs = [np.ma.min, np.ma.max]
    stdfuncs = [np.min, np.max]

    def test_mutation(self):
        # Check that passed array is not modified.
        ndat = _madat.copy()
        for f in self.mafuncs:
            f(ndat)
            assert_equal(ndat, _madat)

    def test_keepdims(self):
        mat = np.eye(3)
        for mf, rf in zip(self.mafuncs, self.stdfuncs):
            for axis in [None, 0, 1]:
                tgt = rf(mat, axis=axis, keepdims=True)
                res = mf(mat, axis=axis, keepdims=True)
                assert_(res.ndim == tgt.ndim)

    def test_out(self):
        mat = np.eye(3)
        for mf, rf in zip(self.mafuncs, self.stdfuncs):
            resout = np.zeros(3)
            tgt = rf(mat, axis=1)
            res = mf(mat, axis=1, out=resout)
            assert_almost_equal(res, resout)
            assert_almost_equal(res, tgt)

    def test_dtype_from_input(self):
        codes = 'efdgFDG'
        for nf, rf in zip(self.mafuncs, self.stdfuncs):
            for c in codes:
                mat = np.eye(3, dtype=c)
                tgt = rf(mat, axis=1).dtype.type
                res = nf(mat, axis=1).dtype.type
                assert_(res is tgt)
                # scalar case
                tgt = rf(mat, axis=None).dtype.type
                res = nf(mat, axis=None).dtype.type
                assert_(res is tgt)

    def test_result_values(self):
        for nf, rf in zip(self.mafuncs, self.stdfuncs):
            tgt = [rf(d) for d in _rdat]
            res = nf(_madat, axis=1)
            assert_almost_equal(res, tgt)

    def test_allmasked(self):
        mat = np.ma.array(np.ones((3,3)), mask=np.ones((3,3)))
        for f in self.mafuncs:
            for axis in [None, 0, 1]:
                assert_(f(mat, axis=axis).mask.all())
            # Check scalars
            assert_(f(np.ma.masked) is np.ma.masked)

    def test_scalar(self):
        for f in self.mafuncs:
            assert_(f(0.) == 0.)

class TestMaskedFunctions_ArgminArgmax(TestCase):

    mafuncs = [np.ma.argmin, np.ma.argmax]

    def test_mutation(self):
        # Check that passed array is not modified.
        ndat = _madat.copy()
        for f in self.mafuncs:
            f(ndat)
            assert_equal(ndat, _madat)

    def test_result_values(self):
        for f, fcmp in zip(self.mafuncs, [np.ma.greater, np.ma.less]):
            for row in _madat:
                ind = f(row)
                val = row[ind]
                assert_(val is not np.ma.masked)
                assert_(not fcmp(val, row).any())
                assert_(np.ma.equal(val, row[:ind]).any() in
                                                          [False, np.ma.masked])

    def test_empty(self):
        mat = np.ma.zeros((0, 3))
        for f in self.mafuncs:
            for axis in [0, None]:
                assert_raises(ValueError, f, mat, axis=axis)
            for axis in [1]:
                res = f(mat, axis=axis)
                assert_equal(res, np.zeros(0))

    def test_scalar(self):
        for f in self.mafuncs:
            assert_(f(0.) == 0.)


class TestMaskedFunctions_IntTypes(TestCase):

    int_types = (np.int8, np.int16, np.int32, np.int64, np.uint8,
                 np.uint16, np.uint32, np.uint64)

    mat = np.ma.array([127, 39, 93, 87, 46])

    def integer_arrays(self):
        for dtype in self.int_types:
            yield self.mat.astype(dtype)

    def test_mamin(self):
        tgt = np.min(self.mat)
        for mat in self.integer_arrays():
            assert_equal(np.ma.min(mat), tgt)

    def test_mamax(self):
        tgt = np.max(self.mat)
        for mat in self.integer_arrays():
            assert_equal(np.ma.max(mat), tgt)

    def test_maargmin(self):
        tgt = np.argmin(self.mat)
        for mat in self.integer_arrays():
            assert_equal(np.ma.argmin(mat), tgt)

    def test_maargmax(self):
        tgt = np.argmax(self.mat)
        for mat in self.integer_arrays():
            assert_equal(np.ma.argmax(mat), tgt)

    def test_masum(self):
        tgt = np.sum(self.mat)
        for mat in self.integer_arrays():
            assert_equal(np.ma.sum(mat), tgt)

    def test_maprod(self):
        tgt = np.prod(self.mat)
        for mat in self.integer_arrays():
            assert_equal(np.ma.prod(mat), tgt)

    def test_mamean(self):
        tgt = np.mean(self.mat)
        for mat in self.integer_arrays():
            assert_equal(np.ma.mean(mat), tgt)

    def test_mavar(self):
        tgt = np.var(self.mat)
        for mat in self.integer_arrays():
            assert_equal(np.ma.var(mat), tgt)

        tgt = np.var(mat, ddof=1)
        for mat in self.integer_arrays():
            assert_equal(np.ma.var(mat, ddof=1), tgt)

    def test_mastd(self):
        tgt = np.std(self.mat)
        for mat in self.integer_arrays():
            assert_equal(np.ma.std(mat), tgt)

        tgt = np.std(self.mat, ddof=1)
        for mat in self.integer_arrays():
            assert_equal(np.ma.std(mat, ddof=1), tgt)


class SharedMaskedFunctionsTestsMixin(object):
    def test_mutation(self):
        # Check that passed array is not modified.
        ndat = _madat.copy()
        for f in self.mafuncs:
            f(ndat)
            assert_equal(ndat, _madat)

    def test_keepdims(self):
        mat = np.eye(3)
        for nf, rf in zip(self.mafuncs, self.stdfuncs):
            for axis in [None, 0, 1]:
                tgt = rf(mat, axis=axis, keepdims=True)
                res = nf(mat, axis=axis, keepdims=True)
                assert_(res.ndim == tgt.ndim)

    def test_out(self):
        mat = np.eye(3)
        for nf, rf in zip(self.mafuncs, self.stdfuncs):
            resout = np.zeros(3)
            tgt = rf(mat, axis=1)
            res = nf(mat, axis=1, out=resout)
            assert_almost_equal(res, resout)
            assert_almost_equal(res, tgt)

    def test_dtype_from_dtype(self):
        mat = np.eye(3)
        codes = 'efdgFDG'
        for nf, rf in zip(self.mafuncs, self.stdfuncs):
            for c in codes:
                tgt = rf(mat, dtype=np.dtype(c), axis=1).dtype.type
                res = nf(mat, dtype=np.dtype(c), axis=1).dtype.type
                assert_(res is tgt)
                # scalar case
                tgt = rf(mat, dtype=np.dtype(c), axis=None).dtype.type
                res = nf(mat, dtype=np.dtype(c), axis=None).dtype.type
                assert_(res is tgt)

    def test_dtype_from_char(self):
        mat = np.eye(3)
        codes = 'efdgFDG'
        for nf, rf in zip(self.mafuncs, self.stdfuncs):
            for c in codes:
                tgt = rf(mat, dtype=c, axis=1).dtype.type
                res = nf(mat, dtype=c, axis=1).dtype.type
                assert_(res is tgt)
                # scalar case
                tgt = rf(mat, dtype=c, axis=None).dtype.type
                res = nf(mat, dtype=c, axis=None).dtype.type
                assert_(res is tgt)

    def test_dtype_from_input(self):
        codes = 'efdgFDG'
        for nf, rf in zip(self.mafuncs, self.stdfuncs):
            for c in codes:
                mat = np.eye(3, dtype=c)
                tgt = rf(mat, axis=1).dtype.type
                res = nf(mat, axis=1).dtype.type
                assert_(res is tgt, "res %s, tgt %s" % (res, tgt))
                # scalar case
                tgt = rf(mat, axis=None).dtype.type
                res = nf(mat, axis=None).dtype.type
                assert_(res is tgt)

    def test_result_values(self):
        for nf, rf in zip(self.mafuncs, self.stdfuncs):
            tgt = [rf(d) for d in _rdat]
            res = nf(_madat, axis=1)
            assert_almost_equal(res, tgt)

    def test_scalar(self):
        for f in self.mafuncs:
            assert_(f(0.) == 0.)

    def test_matrices(self):
        # Check that it works and that type and
        # shape are preserved
        mat = np.matrix(np.eye(3))
        for f in self.mafuncs:
            res = f(mat, axis=0)
            assert_(isinstance(res, np.matrix))
            assert_(res.shape == (1, 3))
            res = f(mat, axis=1)
            assert_(isinstance(res, np.matrix))
            assert_(res.shape == (3, 1))
            res = f(mat)
            assert_(np.isscalar(res))


class TestMaskedFunctions_SumProd(TestCase, SharedMaskedFunctionsTestsMixin):

    mafuncs = [np.ma.sum, np.ma.prod]
    stdfuncs = [np.sum, np.prod]

    def test_allmasked(self):
        res = np.ma.sum([np.ma.masked]*3, axis=None)
        assert_(res is np.ma.masked)

    def test_empty(self):
        for f, tgt_value in zip([np.ma.sum, np.ma.prod], [0, 1]):
            mat = np.zeros((0, 3))
            tgt = [tgt_value]*3
            res = f(mat, axis=0)
            assert_equal(res, tgt)
            tgt = []
            res = f(mat, axis=1)
            assert_equal(res, tgt)
            tgt = tgt_value
            res = f(mat, axis=None)
            assert_equal(res, tgt)


class TestMaskedFunctions_MeanVarStd(TestCase, SharedMaskedFunctionsTestsMixin):

    mafuncs = [np.ma.mean, np.ma.var, np.ma.std]
    stdfuncs = [np.mean, np.var, np.std]

    def test_ddof(self):
        mafuncs = [np.ma.var, np.ma.std]
        stdfuncs = [np.var, np.std]
        for nf, rf in zip(mafuncs, stdfuncs):
            for ddof in [0, 1]:
                tgt = [rf(d, ddof=ddof) for d in _rdat]
                res = nf(_madat, axis=1, ddof=ddof)
                assert_almost_equal(res, tgt)

    def test_ddof_too_big(self):
        mafuncs = [np.ma.var, np.ma.std]
        stdfuncs = [np.var, np.std]
        dsize = [len(d) for d in _rdat]
        for nf, rf in zip(mafuncs, stdfuncs):
            for ddof in range(5):
                tgt = [ddof >= d for d in dsize]
                res = nf(_madat, axis=1, ddof=ddof)
                assert_equal(res.mask, tgt)

    def test_allmasked(self):
        mat = np.ma.array([np.ma.masked]*9).reshape(3, 3)
        for f in self.mafuncs:
            for axis in [None, 0, 1]:
                assert_(f(mat, axis=axis).mask.all())
                # Check scalar
                assert_(f(np.ma.masked) is np.ma.masked)

class TestMaskedFunctions_Median(TestCase):

    def test_mutation(self):
        # Check that passed array is not modified.
        ndat = _madat.copy()
        np.ma.median(ndat)
        assert_equal(ndat, _madat)

    def test_keepdims(self):
        mat = np.eye(3)
        for axis in [None, 0, 1]:
            tgt = np.median(mat, axis=axis, out=None, overwrite_input=False)
            res = np.ma.median(mat, axis=axis, out=None, overwrite_input=False)
            assert_(res.ndim == tgt.ndim)

        d = np.ma.ones((3, 5, 7, 11))
        # Randomly set some elements to NaN:
        w = np.random.random((4, 200)) * np.array(d.shape)[:, None]
        w = w.astype(np.intp)
        d[tuple(w)] = np.ma.masked

        res = np.ma.median(d, axis=None, keepdims=True)
        assert_equal(res.shape, (1, 1, 1, 1))
        res = np.ma.median(d, axis=(0, 1), keepdims=True)
        assert_equal(res.shape, (1, 1, 7, 11))
        res = np.ma.median(d, axis=(0, 3), keepdims=True)
        assert_equal(res.shape, (1, 5, 7, 1))
        res = np.ma.median(d, axis=(1,), keepdims=True)
        assert_equal(res.shape, (3, 1, 7, 11))
        res = np.ma.median(d, axis=(0, 1, 2, 3), keepdims=True)
        assert_equal(res.shape, (1, 1, 1, 1))
        res = np.ma.median(d, axis=(0, 1, 3), keepdims=True)
        assert_equal(res.shape, (1, 1, 7, 1))

    def test_out(self):
        mat = np.random.rand(3, 3)
        nan_mat = np.insert(mat, [0, 2], np.nan, axis=1)
        ma_mat = np.ma.array(nan_mat, mask=np.isnan(nan_mat))
        resout = np.ma.zeros(3)
        tgt = np.median(mat, axis=1)
        res = np.ma.median(ma_mat, axis=1, out=resout)
        assert_almost_equal(res, resout)
        assert_almost_equal(res, tgt)

    def test_small_large(self):
        # test the small and large code paths, current cutoff 400 elements
        for s in [5, 20, 51, 200, 1000]:
            d = np.ma.array(np.random.randn(4, s))
            # Randomly set some elements to NaN:
            w = np.random.randint(0, d.size, size=d.size // 5)
            d.ravel()[w] = np.ma.masked
            d[:,0] = 1.  # ensure at least one good value
            # use normal median without masked to compare
            tgt = []
            for x in d:
                nomask = np.ma.compress(np.ones(x.shape), x.filled(0))
                tgt.append(np.median(nomask, overwrite_input=True))

            assert_array_equal(np.ma.median(d, axis=-1), tgt)

    def test_result_values(self):
            tgt = [np.median(d) for d in _rdat]
            res = np.ma.median(_madat, axis=1)
            assert_almost_equal(res, tgt)

    def test_allmasked(self):
        mat = np.ma.array([np.ma.masked]*9).reshape(3, 3)
        for axis in [None, 0, 1]:
            assert_(np.ma.median(mat, axis=axis).mask.all())
            # Check scalar
            assert_(np.ma.median(np.ma.masked) is np.ma.masked)

    def test_empty(self):
        mat = np.zeros((0, 3))
        for axis in [1]:
            with warnings.catch_warnings(record=True) as w:
                warnings.simplefilter('always')
                assert_equal(np.ma.median(mat, axis=axis), np.zeros([]))
                assert_(len(w) == 0)

    def test_scalar(self):
        assert_(np.ma.median(0.) == 0.)

    def test_extended_axis_invalid(self):
        d = np.ones((3, 5, 7, 11))
        assert_raises(IndexError, np.ma.median, d, axis=-5)
        assert_raises(IndexError, np.ma.median, d, axis=(0, -5))
        assert_raises(IndexError, np.ma.median, d, axis=4)
        assert_raises(IndexError, np.ma.median, d, axis=(0, 4))
        assert_raises(ValueError, np.ma.median, d, axis=(1, 1))


if __name__ == "__main__":
    run_module_suite()
