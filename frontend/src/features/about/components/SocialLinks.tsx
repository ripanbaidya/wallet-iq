import { FaGithub, FaInstagram, FaLinkedin } from "react-icons/fa";
import type { Social } from "../about.types";

interface Props {
  social: Social;
}

interface SocialLinkProps {
  href?: string;
  title: string;
  icon: React.ReactNode;
  label: string;
  hoverClass: string;
  iconHoverClass: string;
  labelHoverClass: string;
}

const SocialLink: React.FC<SocialLinkProps> = ({
  href,
  title,
  icon,
  label,
  hoverClass,
  iconHoverClass,
  labelHoverClass,
}) => (
  <a
    href={href}
    target="_blank"
    rel="noreferrer"
    title={title}
    className={`group flex flex-col items-center gap-1.5 text-gray-400 transition-all ${hoverClass} ${!href ? "cursor-pointer" : ""}`}
  >
    <span
      className={`w-10 h-10 flex items-center justify-center rounded-xl border border-gray-200 transition-all ${iconHoverClass}`}
    >
      {icon}
    </span>
    <span
      className={`text-[10px] text-gray-400 transition-colors ${labelHoverClass}`}
    >
      {label}
    </span>
  </a>
);

const SocialLinks: React.FC<Props> = ({ social }) => (
  <div className="flex justify-center items-center gap-5 sm:gap-8 py-4">
    <SocialLink
      // href={social.github} — disabled until backend serves it
      title="GitHub"
      icon={<FaGithub size={18} />}
      label="GitHub"
      hoverClass="hover:text-gray-900"
      iconHoverClass="group-hover:border-gray-900 group-hover:bg-gray-900 group-hover:text-white"
      labelHoverClass="group-hover:text-gray-700"
    />
    <SocialLink
      href={social.linkedIn ?? "https://www.linkedin.com/in/ripanbaidya/"}
      title="LinkedIn"
      icon={<FaLinkedin size={18} />}
      label="LinkedIn"
      hoverClass="hover:text-blue-600"
      iconHoverClass="group-hover:border-blue-200 group-hover:bg-blue-50"
      labelHoverClass="group-hover:text-blue-500"
    />
    <SocialLink
      href={social.instagram ?? "https://www.instagram.com/futurenoogler"}
      title="Instagram"
      icon={<FaInstagram size={18} />}
      label="Instagram"
      hoverClass="hover:text-pink-500"
      iconHoverClass="group-hover:border-pink-200 group-hover:bg-pink-50"
      labelHoverClass="group-hover:text-pink-500"
    />
  </div>
);

export default SocialLinks;
