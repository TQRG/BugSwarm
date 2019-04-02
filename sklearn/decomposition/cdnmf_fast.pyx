# cython: cdivision=True
# cython: boundscheck=False
# cython: wraparound=False

# Author: Mathieu Blondel, Tom Dupre la Tour
# License: BSD 3 clause

import numpy as np
cimport cython
cimport numpy as np
from libc.math cimport fabs, fmax, fmin


def _update_cdnmf_fast(double[:, ::1] W, double[:, :] HHt, double[:, :] XHt,
                       bint shuffle, int seed):

    cdef double violation = 0
    cdef int n_components = W.shape[1]
    cdef int n_samples = W.shape[0]  # n_features for H update
    cdef double grad, pg, hess
    cdef int j, i, t, r
    cdef np.ndarray[long, ndim=1] permutation_array
    cdef long* permutation = NULL

    if shuffle:
        rng = np.random.RandomState(seed)
        permutation_array = rng.permutation(n_samples)
    else:
        permutation_array = np.arange(n_samples)
    permutation = <long*> permutation_array.data

    with nogil:
        for j in range(n_samples):
            i = permutation[j]
            for t in range(n_components):

                # gradient = GW[t, i] where GW = np.dot(W, HHt) - XHt
                grad = - XHt[i, t]

                for r in range(n_components):
                    grad += HHt[t, r] * W[i, r]

                # projected gradient
                pg = fmin(0, grad) if W[i, t] == 0 else grad
                violation += fabs(pg)

                # Hessian
                hess = HHt[t, t]

                if hess != 0:
                    W[i, t] = fmax(W[i, t] - grad / hess, 0)
                
    return violation


def _update_greedy_cdnmf_fast(double[:, ::1] W, double[:, :] HHt,
                              double[:, :] G, int max_inner, double tol,
                              bint shuffle, int seed):
    cdef double violation = 0.
    cdef double pg
    cdef int n_samples = W.shape[0]  # n_features for H update
    cdef int n_components = W.shape[1]
    cdef int qi
    cdef int j, i, t, r
    cdef double s = 0.
    cdef double p_init = 0.
    cdef double Di_max, Dir
    cdef int[:] q = np.zeros(n_samples, dtype=np.int32)
    cdef double[:, ::1] S = np.zeros((n_samples, n_components))
    cdef double[:, ::1] D = np.zeros((n_samples, n_components))
    cdef np.ndarray[long, ndim=1] permutation_array
    cdef long* permutation = NULL

    if shuffle:
        rng = np.random.RandomState(seed)
        permutation_array = rng.permutation(n_samples)
    else:
        permutation_array = np.arange(n_samples)
    permutation = <long*> permutation_array.data

    # compute S, D and p_init
    with nogil:
        for j in range(n_samples):
            i = permutation[j]
            Di_max = 0.
            q[i] = 0
            for r in range(n_components):
                # Step amplitude
                if HHt[r, r] != 0:
                    S[i, r] = fmax(W[i, r] - G[i, r] / HHt[r, r], 0.) - W[i, r]
                else:
                    S[i, r] = 0.
                # Loss Difference
                D[i, r] = -(G[i, r] + HHt[r, r] / 2. * S[i, r]) * S[i, r]

                # find q[i] = argmax_r(D[i, r])
                if D[i, r] > Di_max:
                    q[i] = r
                    Di_max = D[i, r]
            # find p_init = max(D)
            if Di_max > p_init:
                p_init = Di_max
                
    if p_init == 0.:
        return 0.

    with nogil:
        for i in range(n_samples):
            qi = q[i]
            Di_max = D[i, qi]

            for t in range(max_inner):
                if Di_max < tol * p_init:
                    break

                # projected gradient for violation
                pg = fmin(0, G[i, qi]) if W[i, qi] == 0 else G[i, qi]
                violation += fabs(pg)

                s = S[i, qi]
                W[i, qi] += s

                for r in range(n_components):
                    G[i, r] += s * HHt[qi, r]
                
                for r in range(n_components):
                    if HHt[r, r] != 0:
                        S[i, r] = (fmax(W[i, r] - G[i, r] / HHt[r, r], 0)
                                   - W[i, r])
                    else:
                        S[i, r] = 0.
                    Dir = -(G[i, r] + HHt[r, r] / 2. * S[i, r]) * S[i, r]

                    # find qi = argmax_r(D[i, r])
                    if r == 0 or Dir > Di_max:
                        qi = r
                        Di_max = Dir

    return violation
