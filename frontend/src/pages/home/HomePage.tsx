/**
 * Imports all home-specific sections and renders them in order.
 *
 *   HomeNavbar        — fixed nav with scroll-aware styling
 *   HeroSection       — headline, typewriter, CTAs
 *   StatsSection      — animated social-proof numbers
 *   FeaturesSection   — 6-feature grid
 *   HowItWorksSection — 4-step guide + mock AI chat
 *   TestimonialsSection — dual infinite marquee
 *   CTASection        — bottom conversion banner
 *   HomeFooter        — links + copyright
 */

import HomeNavbar from "../../components/home/HomeNavbar";
import HeroSection from "../../components/home/HeroSection";
import StatsSection from "../../components/home/StatsSection";
import FeaturesSection from "../../components/home/FeaturesSection";
import HowItWorksSection from "../../components/home/HowItWorksSection";
import TestimonialsSection from "../../components/home/TestimonialsSection";
import { CTASection, HomeFooter } from "../../components/home/HomeFooter";

export default function HomePage() {
  return (
    <div className="bg-white text-gray-900 font-sans antialiased">
      <HomeNavbar />
      <HeroSection />
      <StatsSection />
      <FeaturesSection />
      <HowItWorksSection />
      <TestimonialsSection />
      <CTASection />
      <HomeFooter />
    </div>
  );
}
