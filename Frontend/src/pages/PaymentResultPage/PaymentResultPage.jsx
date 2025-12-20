// import React, { useEffect, useState } from "react";
// import { useSearchParams, useNavigate } from "react-router-dom";
// import styles from "./PaymentResultPage.module.css";

// const PaymentResultPage = () => {
//   const [searchParams] = useSearchParams();
//   const navigate = useNavigate();
//   const [status, setStatus] = useState("processing"); // processing | success | failed

//   useEffect(() => {
//     // 1. Lấy mã phản hồi từ VNPAY (vnp_ResponseCode=00 là thành công)
//     const vnpResponseCode = searchParams.get("vnp_ResponseCode");

//     // 2. Hoặc lấy status từ Backend nếu Backend redirect kèm param custom
//     const backendStatus = searchParams.get("status");

//     if (backendStatus === "success" || vnpResponseCode === "00") {
//       setStatus("success");
//     } else if (
//       backendStatus === "failed" ||
//       (vnpResponseCode && vnpResponseCode !== "00")
//     ) {
//       setStatus("failed");
//     } else {
//       // Mặc định nếu không rõ ràng thì coi như thất bại để an toàn
//       setStatus("failed");
//     }
//   }, [searchParams]);

//   return (
//     <div className={styles.container}>
//       {status === "success" ? (
//         <div className={styles.resultContent}>
//           <div className={styles.iconWrapper}>
//             <i className={`fa-solid fa-circle-check ${styles.iconSuccess}`}></i>
//           </div>
//           <h2 className={styles.title}>Thanh toán thành công!</h2>
//           <p className={styles.message}>
//             Cảm ơn bạn đã mua hàng. <br />
//             Đơn hàng của bạn đã được hệ thống ghi nhận và đang xử lý.
//           </p>
//           <button
//             className={`${styles.btn} ${styles.btnPrimary}`}
//             onClick={() => navigate("/order-history")}
//           >
//             Xem lịch sử đơn hàng
//           </button>
//         </div>
//       ) : (
//         <div className={styles.resultContent}>
//           <div className={styles.iconWrapper}>
//             <i className={`fa-solid fa-circle-xmark ${styles.iconFailed}`}></i>
//           </div>
//           <h2 className={styles.title}>Thanh toán thất bại</h2>
//           <p className={styles.message}>
//             Giao dịch bị hủy hoặc có lỗi xảy ra trong quá trình thanh toán.
//             <br />
//             Vui lòng thử lại hoặc chọn phương thức thanh toán khác.
//           </p>
//           <div className={styles.btnGroup}>
//             <button
//               className={`${styles.btn} ${styles.btnSecondary}`}
//               onClick={() => navigate("/")}
//             >
//               Về trang chủ
//             </button>
//             <button
//               className={`${styles.btn} ${styles.btnPrimary}`}
//               onClick={() => navigate("/cart")} // Quay lại giỏ hàng hoặc trang checkout
//             >
//               Thử lại
//             </button>
//           </div>
//         </div>
//       )}
//     </div>
//   );
// };

// export default PaymentResultPage;
import React, { useEffect, useState, useRef } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import styles from "./PaymentResultPage.module.css";
import orderService from "../../services/orderService";

const PaymentResultPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  // State quản lý trạng thái hiển thị
  const [status, setStatus] = useState("processing"); // processing | success | failed
  const [message, setMessage] = useState("Đang xử lý kết quả thanh toán...");

  // Dùng useRef để tránh gọi API 2 lần trong React.StrictMode
  const hasCalledAPI = useRef(false);

  // Lấy thông tin từ URL
  const vnpResponseCode = searchParams.get("vnp_ResponseCode");
  const orderId = searchParams.get("vnp_TxnRef"); // Đây là mã đơn hàng (vnp_TxnRef)

  useEffect(() => {
    const verifyPayment = async () => {
      // Nếu đã gọi rồi thì thôi
      if (hasCalledAPI.current) return;
      hasCalledAPI.current = true;

      // 1. Kiểm tra mã phản hồi từ URL trước
      if (vnpResponseCode !== "00") {
        setStatus("failed");
        setMessage("Giao dịch bị hủy hoặc thất bại tại cổng thanh toán.");
        return;
      }

      // 2. Nếu mã 00 (Thành công ở FE), gọi về Backend để xác thực Checksum
      try {
        // Lấy toàn bộ query string: ?vnp_Amount=...&...
        const queryString = window.location.search;

        // Gọi API Backend
        const res = await orderService.vnpayReturn(queryString);

        // 3. Xử lý kết quả từ Backend trả về
        if (res.success) {
          setStatus("success");
          setMessage("Thanh toán thành công!");
        } else {
          setStatus("failed");
          setMessage(res.message || "Xác thực giao dịch thất bại.");
        }
      } catch (error) {
        console.error(error);
        setStatus("failed");
        setMessage("Lỗi kết nối đến hệ thống xác thực.");
      }
    };

    verifyPayment();
  }, [vnpResponseCode]);

  return (
    <div className={styles.container}>
      {/* TRẠNG THÁI: ĐANG XỬ LÝ */}
      {status === "processing" && (
        <div className={styles.resultContent}>
          <div className={styles.spinner}></div>{" "}
          {/* Bạn có thể thêm css spinner quay quay */}
          <h2 className={styles.title} style={{ color: "#666" }}>
            Đang xác thực...
          </h2>
          <p className={styles.message}>Vui lòng không tắt trình duyệt.</p>
        </div>
      )}

      {/* TRẠNG THÁI: THÀNH CÔNG */}
      {status === "success" && (
        <div className={styles.resultContent}>
          <div className={styles.iconWrapper}>
            <i className={`fa-solid fa-circle-check ${styles.iconSuccess}`}></i>
          </div>
          <h2 className={styles.title}>Thanh toán thành công!</h2>

          <div
            style={{
              background: "#f9f9f9",
              padding: "15px",
              borderRadius: "8px",
              margin: "20px 0",
            }}
          >
            <p style={{ margin: "5px 0", fontSize: "15px" }}>Mã thanh toán:</p>
            <h3 style={{ margin: "0", color: "#b5292f", fontSize: "24px" }}>
              #{orderId}
            </h3>
          </div>

          <p className={styles.message}>
            Cảm ơn bạn đã mua hàng. <br />
            Đơn hàng của bạn đã được hệ thống ghi nhận và đang xử lý.
          </p>
          <button
            className={`${styles.btn} ${styles.btnPrimary}`}
            onClick={() => navigate("/order-history")}
          >
            Xem lịch sử đơn hàng
          </button>
        </div>
      )}

      {/* TRẠNG THÁI: THẤT BẠI */}
      {status === "failed" && (
        <div className={styles.resultContent}>
          <div className={styles.iconWrapper}>
            <i className={`fa-solid fa-circle-xmark ${styles.iconFailed}`}></i>
          </div>
          <h2 className={styles.title}>Thanh toán thất bại</h2>

          {orderId && (
            <p style={{ fontWeight: "bold", color: "#555" }}>
              Đơn hàng: #{orderId}
            </p>
          )}

          <p className={styles.message}>
            {message} <br />
            Vui lòng thử lại hoặc chọn phương thức thanh toán khác.
          </p>
          <div className={styles.btnGroup}>
            <button
              className={`${styles.btn} ${styles.btnSecondary}`}
              onClick={() => navigate("/")}
            >
              Về trang chủ
            </button>
            <button
              className={`${styles.btn} ${styles.btnPrimary}`}
              onClick={() => navigate("/checkout")} // Quay lại checkout để thanh toán lại
            >
              Thử lại
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default PaymentResultPage;
