/** sweetAlert_0.0.1.js 사용방법
 *     <!-- sweetalert2 -->
 *     <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
 *     <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
 *     <script src="js/sweetAlert_0.0.1.js"></script>
 *
 * */

    function alertDefaultSuccess(title, text) {
        return Swal.fire({
            icon: 'success',
            title: title,
            text: text,
        }).then((result) => {
             result.isConfirmed;
        });
    }
    function alertDefaultError(title, text) {
        return Swal.fire({
            icon: 'error',
            title: title,
            text: text,
        }).then((result) => {
            result.isConfirmed;
        });
    }

    function alertDefaultWarning(title, text) {
        return Swal.fire({
            icon: 'warning',
            title: title,
            text: text,
        }).then((result) => {
            result.isConfirmed;
        });
    }

    function alertDefaultInfo(title, text) {
        return Swal.fire({
            icon: 'info',
            title: title,
            text: text,
        }).then((result) => {
            result.isConfirmed;
        });
    }

    function alertDefaultQuestion(title, text) {
        return Swal.fire({
            icon: 'question',
            title: title,
            text: text,
        }).then((result) => {
            result.isConfirmed;
        });
    }

    function alertConfirmQuestion(title, text, confirmButtonText, cancelButtonText) {
        return Swal.fire({
            title: title,
            text: text,
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: confirmButtonText,
            cancelButtonText: cancelButtonText,
            reverseButtons: false, // 버튼 순서 거꾸로

        }).then((result) => {
            return result.isConfirmed;
        });
    }

    function alertConfirmWarning(title, text, confirmButtonText, cancelButtonText) {
        return Swal.fire({
            title: title,
            text: text,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: confirmButtonText,
            cancelButtonText: cancelButtonText,
            reverseButtons: false, // 버튼 순서 거꾸로

        }).then((result) => {
            return result.isConfirmed;
        });
    }