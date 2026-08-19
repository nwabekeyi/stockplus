import { Link } from "react-router";
import { IconPackage } from "../common/icons";
import { APP_NAME } from "../../constants";

export default function Logo({
  className = "",
  showText = true,
  textClassName = "text-xl font-bold text-gray-900 tracking-tight",
}: {
  className?: string;
  showText?: boolean;
  textClassName?: string;
}) {
  return (
    <Link to="/" className={`inline-flex items-center gap-2 ${className}`}>
      <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center shadow-sm">
        <IconPackage className="w-4 h-4 text-white" />
      </div>
      {showText && <span className={textClassName}>{APP_NAME}</span>}
    </Link>
  );
}
