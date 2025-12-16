import logo from "../../assets/images/logo/logo.png";
import { useNavigate } from "react-router-dom";
import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function HeaderComponent() {
  const { auth, logout, isLoggedIn } = useAuth();
  const navigate = useNavigate();

  return (
    <>
      <header>
        {/* Header top */}
        <div className="header-top">
          <div className="container">
            <div className="header-top-left">
              <ul className="header-top-list">
                <li>
                  <a href="">
                    <i className="fa-regular fa-phone"></i> 0123 456 789 (miễn
                    phí)
                  </a>
                </li>
                <li>
                  <a href="">
                    <i className="fa-light fa-location-dot"></i> Xem vị trí cửa
                    hàng
                  </a>
                </li>
              </ul>
            </div>
            <div className="header-top-right">
              <ul className="header-top-list">
                <li>
                  <a href="">Giới thiệu</a>
                </li>
                <li>
                  <a href="">Cửa hàng</a>
                </li>
                <li>
                  <a href="">Chính sách</a>
                </li>
              </ul>
            </div>
          </div>
        </div>

        {/* Header middle */}
        <div className="header-middle">
          <div className="container">
            {/* Logo */}
            <div className="header-middle-left">
              <div className="header-logo">
                <a href="/">
                  <img src={logo} alt="" className="header-logo-img" />
                </a>
              </div>
            </div>

            {/* Search */}
            <div className="header-middle-center">
              <form action="" className="form-search">
                <span className="search-btn">
                  <i className="fa-light fa-magnifying-glass"></i>
                </span>
                <input
                  type="text"
                  className="form-search-input"
                  placeholder="Tìm kiếm món ăn..."
                />
                <button type="button" className="filter-btn">
                  <i className="fa-light fa-filter-list"></i>
                  <span>Lọc</span>
                </button>
              </form>
            </div>

            {/* Right menu */}
            <div className="header-middle-right">
              <ul className="header-middle-right-list">
                {/* User */}
                <li className="header-middle-right-item dropdown open">
                  <i className="fa-light fa-user"></i>
                  <div className="auth-container">
                    {!isLoggedIn ? (
                      <>
                        <span className="text-dndk">Đăng nhập / Đăng ký</span>
                        <span className="text-tk">
                          Tài khoản{" "}
                          <i className="fa-sharp fa-solid fa-caret-down"></i>
                        </span>
                      </>
                    ) : (
                      <>
                        <span className="text-dndk">Tài khoản</span>
                        <span className="text-tk">
                          {auth.accountName}{" "}
                          <i className="fa-sharp fa-solid fa-caret-down"></i>
                        </span>
                      </>
                    )}
                  </div>
                  <ul className="header-middle-right-menu">
                    {!isLoggedIn ? (
                      <>
                        <li>
                          <Link id="login" to="/auth?action=login">
                            <i className="fa-light fa-right-to-bracket"></i>{" "}
                            Đăng nhập
                          </Link>
                        </li>
                        <li>
                          <Link id="signup" to="/auth?action=register">
                            <i className="fa-light fa-user-plus"></i> Đăng ký
                          </Link>
                        </li>
                      </>
                    ) : (
                      <>
                        <li>
                          <a href="#">
                            <i className="fa-light fa-circle-user"></i> Tài
                            khoản của tôi
                          </a>
                        </li>
                        <li>
                          <a href="#">
                            <i className="fa-regular fa-bags-shopping"></i> Đơn
                            hàng đã mua
                          </a>
                        </li>
                        <li className="border">
                          <a
                            id="logout"
                            href="javascript:;"
                            onClick={(e) => {
                              e.preventDefault(); // chặn reload
                              logout();
                              navigate("/");
                            }}
                          >
                            <i className="fa-light fa-right-from-bracket"></i>{" "}
                            Thoát tài khoản
                          </a>
                        </li>
                      </>
                    )}
                  </ul>
                </li>

                {/* Cart */}
                <li
                  className="header-middle-right-item open"
                  // onClick={openCart}
                >
                  <div className="cart-icon-menu">
                    <i className="fa-light fa-basket-shopping"></i>
                    {/* Hiển thị số lượng một cách an toàn */}
                    <span className="count-product-cart">{/**/ 0}</span>
                  </div>
                  <span>Giỏ hàng</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </header>
    </>
  );
}
